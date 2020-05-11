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
package org.apache.kafka.connect.nullsink;

//import org.apache.kafka.connect.errors.ConnectException;
//import org.apache.kafka.connect.errors.DataException;
//import org.apache.kafka.connect.sink.SinkRecord;
//import org.apache.kafka.connect.sink.SinkTask;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Map;

//public class NullSinkTask extends SinkTask {
public class NullSinkTask {
//
//    private static final Logger log = LoggerFactory.getLogger(NullSinkTask.class);
//
//    private static final long CHECKPOINT_DELTA = 50_000;
//
//    private FileOutputStream fos;
//    private long numRecordsPut;
//    private boolean start = true;
//    private long nextCheckpoint = 50_000;
//
//    @Override
//    public String version() {
//        return "1.0.0";
//    }
//
//    @Override
//    public void start(Map<String, String> props) {
//        log.info("Starting task {}", this.getClass().getName());
//        try {
//            fos = new FileOutputStream("/dev/null");
//        } catch (FileNotFoundException e) {
//            log.error("Could not open /dev/null");
//            throw new ConnectException(e);
//        }
//    }
//
//    @Override
//    public void put(Collection<SinkRecord> records) {
//        if (start) {
//            log.info("Starting");
//            start = false;
//        }
//        for (SinkRecord record : records) {
//            put(record);
//        }
//
//        numRecordsPut += records.size();
//        if (numRecordsPut > nextCheckpoint) {
//            nextCheckpoint += CHECKPOINT_DELTA;
//            log.info("Added {} records. Next checkpoint at {}", numRecordsPut, nextCheckpoint);
//        }
//    }
//
//    // visible for testing
//    void put(SinkRecord record) {
//        byte[] bytes;
//        if (record.value() instanceof byte[]) {
//            bytes = (byte[]) record.value();
//        } else {
//            throw new DataException("unrecognized type "
//                    + (record.value() != null ? record.value().getClass().getName() : "null object"));
//        }
//
//        try {
//            fos.write(bytes);
//        } catch (IOException e) {
//            log.error("Could not write " + bytes.length + " to /dev/null");
//        }
//    }
//
//    @Override
//    public void stop() {
//        log.info("numRecords={}", numRecordsPut);
//        log.info("Closing /dev/null and stopping connector {}", this.getClass().getName());
//        try {
//            fos.close();
//        } catch (IOException e) {
//            log.debug("Could not close file?!");
//        }
//    }
}
