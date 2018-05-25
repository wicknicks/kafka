/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.errors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.singleton;

/**
 * Write the original consumed record into a dead letter queue. The dead letter queue is a Kafka topic located
 * on the same cluster used by the worker to maintain internal topics. Each connector gets their own dead letter
 * queue topic. By default, the topic name is not set, and if the connector config doesn't specify one, this
 * feature is disabled.
 */
public class DLQReporter implements ErrorReporter {

    private static final Logger log = LoggerFactory.getLogger(DLQReporter.class);

    private static final int ADMIN_OPERATIONS_TIMEOUT_MILLIS = 10000;
    private static final int DLQ_MAX_DESIRED_REPLICATION_FACTOR = 3;
    private static final int DLQ_NUM_DESIRED_PARTITIONS = 1;

    public static final String PREFIX = "errors.deadletterqueue";

    public static final String DLQ_TOPIC_NAME = "topic.name";
    public static final String DLQ_TOPIC_NAME_DOC = "The name of the topic where these messages are written to.";
    public static final String DLQ_TOPIC_DEFAULT = "";

    public static final String DLQ_CONTEXT_HEADERS_ENABLE = "context.headers.enable";
    public static final String DLQ_CONTEXT_HEADERS_ENABLE_DOC = "If true, add headers containing error context.";
    public static final boolean DLQ_CONTEXT_HEADERS_ENABLE_DEFAULT = false;

    public static final String ERROR_HEADER_PREFIX = "__connect.errors";
    public static final String ERROR_HEADER_ORIG_TOPIC = "topic";
    public static final String ERROR_HEADER_ORIG_PARTITION = "partition";
    public static final String ERROR_HEADER_ORIG_OFFSET = "offset";
    public static final String ERROR_HEADER_CONNECTOR_NAME = "connector.name";
    public static final String ERROR_HEADER_TASK_ID = "task.id";
    public static final String ERROR_HEADER_STAGE = "stage";
    public static final String ERROR_HEADER_EXECUTING_CLASS = "class.name";
    public static final String ERROR_HEADER_EXECPTION = "exception.class.name";
    public static final String ERROR_HEADER_EXECPTION_MESSAGE = "exception.message";
    public static final String ERROR_HEADER_EXECPTION_STACK_TRACE = "exception.stacktrace";

    private final ConnectorTaskId id;
    private final KafkaProducer<byte[], byte[]> kafkaProducer;

    private DLQReporterConfig config;
    private ErrorHandlingMetrics errorHandlingMetrics;

    static ConfigDef getConfigDef() {
        return new ConfigDef()
                .define(DLQ_TOPIC_NAME, ConfigDef.Type.STRING, DLQ_TOPIC_DEFAULT, ConfigDef.Importance.HIGH, DLQ_TOPIC_NAME_DOC)
                .define(DLQ_CONTEXT_HEADERS_ENABLE, ConfigDef.Type.BOOLEAN, DLQ_CONTEXT_HEADERS_ENABLE_DEFAULT,
                        ConfigDef.Importance.HIGH, DLQ_CONTEXT_HEADERS_ENABLE_DOC);
    }

    public static DLQReporter createAndSetup(ConnectorTaskId id, WorkerConfig workerConfig, ConnectorConfig connConfig, Map<String, Object> producerProps) {
        String topic = connConfig.getString(PREFIX + "." + DLQ_TOPIC_NAME);

        try (AdminClient admin = AdminClient.create(workerConfig.originals())) {
            if (!admin.listTopics().names().get(ADMIN_OPERATIONS_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).contains(topic)) {
                log.error("Topic {} doesn't exist. Will attempt to create topic.", topic);
                int maxReplicationFactor = admin.describeCluster().nodes().get(ADMIN_OPERATIONS_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).size();
                int replicationFactor = Math.min(maxReplicationFactor, DLQ_MAX_DESIRED_REPLICATION_FACTOR);
                NewTopic schemaTopicRequest = new NewTopic(topic, DLQ_NUM_DESIRED_PARTITIONS, (short) replicationFactor);
                admin.createTopics(singleton(schemaTopicRequest)).all().get(ADMIN_OPERATIONS_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException | InterruptedException e) {
            throw new ConnectException("Could not initialize dead letter queue with topic=" + topic, e);
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new ConnectException("Could not initialize dead letter queue with topic=" + topic, e);
            }
        }

        KafkaProducer<byte[], byte[]> dlqProducer = new KafkaProducer<>(producerProps);
        return new DLQReporter(id, dlqProducer);
    }

    /**
     * Initialize the dead letter queue reporter.
     *
     * @param kafkaProducer a Kafka Producer to produce the original consumed records.
     */
    DLQReporter(ConnectorTaskId id, KafkaProducer<byte[], byte[]> kafkaProducer) {
        this.id = id;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        config = new DLQReporterConfig(configs);
    }

    @Override
    public void setMetrics(ErrorHandlingMetrics errorHandlingMetrics) {
        this.errorHandlingMetrics = errorHandlingMetrics;
    }

    /**
     * @param context write the record from {@link ProcessingContext#consumerRecord()} into the dead letter queue.
     */
    public void report(ProcessingContext context) {
        if (config.topic().isEmpty()) {
            return;
        }

        errorHandlingMetrics.recordDeadLetterQueueProduceRequest();

        ConsumerRecord<byte[], byte[]> originalMessage = context.consumerRecord();
        if (originalMessage == null) {
            errorHandlingMetrics.recordDeadLetterQueueProduceFailed();
            return;
        }

        ProducerRecord<byte[], byte[]> producerRecord;
        if (originalMessage.timestamp() > 0) {
            producerRecord = new ProducerRecord<>(config.topic(), null, originalMessage.timestamp(),
                    originalMessage.key(), originalMessage.value(), originalMessage.headers());
        } else {
            producerRecord = new ProducerRecord<>(config.topic(), null,
                    originalMessage.key(), originalMessage.value(), originalMessage.headers());
        }

        if (config.enableContextHeaders()) {
            populateContextHeaders(producerRecord, context);
        }

        this.kafkaProducer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Could not produce message to dead letter queue. topic=" + config.topic(), exception);
                errorHandlingMetrics.recordDeadLetterQueueProduceFailed();
            }
        });
    }

    // Visible for testing
    void populateContextHeaders(ProducerRecord<byte[], byte[]> producerRecord, ProcessingContext context) {
        String topic = "";
        int partition = -1;
        long offset = -1;
        if (context.consumerRecord() != null) {
            topic = context.consumerRecord().topic();
            partition = context.consumerRecord().partition();
            offset = context.consumerRecord().offset();
        } else if (context.sourceRecord() != null) {
            topic = context.sourceRecord().topic();
            partition = context.sourceRecord().kafkaPartition();
        }

        add(producerRecord.headers(), prefix(ERROR_HEADER_ORIG_TOPIC), topic);
        add(producerRecord.headers(), prefix(ERROR_HEADER_ORIG_PARTITION), String.valueOf(partition));
        add(producerRecord.headers(), prefix(ERROR_HEADER_ORIG_OFFSET), String.valueOf(offset));
        add(producerRecord.headers(), prefix(ERROR_HEADER_CONNECTOR_NAME), id.connector());
        add(producerRecord.headers(), prefix(ERROR_HEADER_TASK_ID), String.valueOf(id.task()));
        add(producerRecord.headers(), prefix(ERROR_HEADER_STAGE), context.stage().name());
        add(producerRecord.headers(), prefix(ERROR_HEADER_EXECUTING_CLASS), context.executingClass().getName());
        if (context.error() != null) {
            add(producerRecord.headers(), prefix(ERROR_HEADER_EXECPTION), context.error().getClass().getName());
            add(producerRecord.headers(), prefix(ERROR_HEADER_EXECPTION_MESSAGE), context.error().getMessage());
            byte[] trace;
            if ((trace = stacktrace(context.error())) != null) {
                add(producerRecord.headers(), prefix(ERROR_HEADER_EXECPTION_STACK_TRACE), trace);
            }
        }
    }

    private byte[] stacktrace(Throwable error) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PrintStream stream = new PrintStream(bos, true, "UTF-8");
            error.printStackTrace(stream);
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Could not serialize stacktrace.", e);
        }
        return null;
    }

    private void add(Headers headers, String key, String value) {
        add(headers, key, value.getBytes(Charset.forName("UTF-8")));
    }

    private void add(Headers headers, String key, byte[] value) {
        if (headers.lastHeader(key) != null) {
            headers.add(key, value);
        } else {
            log.error("Header already contains the '" + key + "' key.");
        }
    }

    private String prefix(String errorHeaderSuffix) {
        return ERROR_HEADER_PREFIX + "." + errorHeaderSuffix;
    }

    static class DLQReporterConfig extends AbstractConfig {
        public DLQReporterConfig(Map<?, ?> originals) {
            super(getConfigDef(), originals, true);
        }

        /**
         * @return name of the dead letter queue topic.
         */
        public String topic() {
            return getString(DLQ_TOPIC_NAME);
        }

        public boolean enableContextHeaders() {
            return getBoolean(DLQ_CONTEXT_HEADERS_ENABLE);
        }
    }
}
