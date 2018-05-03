/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.handlers;

import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class DLQHandlerConfig extends RetryNTimesHandlerConfig {

    protected static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    protected static final String BOOTSTRAP_SERVERS_DOC = "bootstrap servers for the Kafka cluster which will contain the DLQ topic";

    protected static final String DLQ_TOPIC = "dlq.topic";
    protected static final String DLQ_TOPIC_DOC = "name of the topic where bad records along with the failure context will be written to";

    protected static final String DLQ_PARTITIONS = "dlq.partitions";
    protected static final int DLQ_PARTITIONS_DEFAULT = 5;
    protected static final String DLQ_PARTITIONS_DOC = "number of partitions for the DLQ topic";

    protected static final String DLQ_REPLICATION_FACTOR = "dlq.replication_factor";
    protected static final int DLQ_REPLICATION_FACTOR_DEFAULT = 3;
    protected static final String DLQ_REPLICATION_FACTOR_DOC = "the replication factor for the DLQ topic";

    static ConfigDef DLQCONFIG = new ConfigDef(CONFIG)
            .define(BOOTSTRAP_SERVERS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, BOOTSTRAP_SERVERS_DOC)
            .define(DLQ_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, DLQ_TOPIC_DOC)
            .define(DLQ_PARTITIONS, ConfigDef.Type.STRING, DLQ_PARTITIONS_DEFAULT,
                    ConfigDef.Range.atLeast(0), ConfigDef.Importance.MEDIUM, BOOTSTRAP_SERVERS_DOC)
            .define(DLQ_REPLICATION_FACTOR, ConfigDef.Type.STRING, DLQ_REPLICATION_FACTOR_DEFAULT,
                    ConfigDef.Range.atLeast(0), ConfigDef.Importance.MEDIUM, BOOTSTRAP_SERVERS_DOC);

    public DLQHandlerConfig(Map<?, ?> originals) {
        super(DLQCONFIG, originals);
    }

    public String bootstrapServers() {
        return getString(BOOTSTRAP_SERVERS);
    }

    public String topic() {
        return getString(DLQ_TOPIC);
    }

    public int dlqTopicReplicationFactor() {
        return getInt(DLQ_REPLICATION_FACTOR);
    }

    public int dlqTopicNumPartitions() {
        return getInt(DLQ_PARTITIONS);
    }
}
