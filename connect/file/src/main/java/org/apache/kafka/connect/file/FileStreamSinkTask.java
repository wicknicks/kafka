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
package org.apache.kafka.connect.file;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;

/**
 * FileStreamSinkTask writes records to stdout or a file.
 */
public class FileStreamSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(FileStreamSinkTask.class);

    private String filename;
    private PrintStream outputStream;
    private ErrantRecordReporter reporter;

    public FileStreamSinkTask() {
    }

    // for testing
    public FileStreamSinkTask(PrintStream outputStream) {
        filename = null;
        this.outputStream = outputStream;
    }

    public void errantRecordReporter(ErrantRecordReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String version() {
        return new FileStreamSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        /*
        without this try catch block, we observe that the worker starts up correctly, but starting a connector,
        and then the task fails with the error:
         [2020-05-16 18:51:58,723] ERROR WorkerSinkTask{id=fs-0} Task threw an uncaught and unrecoverable exception (org.apache.kafka.connect.runtime.WorkerTask:186)
         java.lang.NoSuchMethodError: org.apache.kafka.connect.sink.SinkTaskContext.reporter()Lorg/apache/kafka/connect/sink/ErrantRecordReporter;
         at org.apache.kafka.connect.file.FileStreamSinkTask.start(FileStreamSinkTask.java:67)
         at org.apache.kafka.connect.runtime.WorkerSinkTask.initializeAndStart(WorkerSinkTask.java:305)
        */
        try {
            this.reporter = context.reporter();
        } catch (NoSuchMethodError e) {
            log.warn("error reporter not available. running with an older runtime?");
        }

        filename = props.get(FileStreamSinkConnector.FILE_CONFIG);
        if (filename == null) {
            outputStream = System.out;
        } else {
            try {
                outputStream = new PrintStream(
                        Files.newOutputStream(Paths.get(filename), StandardOpenOption.CREATE, StandardOpenOption.APPEND),
                        false,
                        StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                throw new ConnectException("Couldn't find or create file '" + filename + "' for FileStreamSinkTask", e);
            }
        }
    }

    @Override
    public void put(Collection<SinkRecord> sinkRecords) {
        if (sinkRecords.isEmpty()) {
            log.info("put() called with empty collection. returning.");
            return;
        }

        for (SinkRecord record : sinkRecords) {
            log.trace("Writing line to {}: {}", logFilename(), record.value());
            String writableStr = String.valueOf(record.value());
            if ("fail".equalsIgnoreCase(writableStr)) {
                reportBadRecord(record);
                continue;
            }
            outputStream.println(writableStr);
        }
    }

    private void reportBadRecord(SinkRecord record) {
        /* error raised here in older version of runtime is:
        [2020-05-16 18:58:04,438] WARN Could not report error because of compatibility issues (org.apache.kafka.connect.file.FileStreamSinkTask:116)
        java.lang.NoClassDefFoundError: org/apache/kafka/connect/sink/ErrantRecordReporter
            at org.apache.kafka.connect.file.FileStreamSinkTask.reportBadRecord(FileStreamSinkTask.java:114)
            at org.apache.kafka.connect.file.FileStreamSinkTask.put(FileStreamSinkTask.java:105)
            at org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:546)
            at org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:326)
            at org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:228)
            at org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:196)
            at org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:184)
            at org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:234)
            at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
            at java.util.concurrent.FutureTask.run(FutureTask.java:266)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
            at java.lang.Thread.run(Thread.java:748)
        Caused by: java.lang.ClassNotFoundException: org.apache.kafka.connect.sink.ErrantRecordReporter
            at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
            at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
         */
        try {
            reporter.report(record, new RuntimeException("bad record, value=" + record.value()));
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            // in a real connector, we would probably not want to print out the entire stacktrace.
            log.warn("Could not report error because of compatibility issues", e);
        }
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
        log.trace("Flushing output stream for {}", logFilename());
        outputStream.flush();
    }

    @Override
    public void stop() {
        if (outputStream != null && outputStream != System.out)
            outputStream.close();
    }

    private String logFilename() {
        return filename == null ? "stdout" : filename;
    }
}
