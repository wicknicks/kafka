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

//import org.apache.kafka.common.config.ConfigDef;
//import org.apache.kafka.connect.connector.Task;
//import org.apache.kafka.connect.data.Schema;
//import org.apache.kafka.connect.sink.SinkConnector;
//import org.apache.kafka.connect.sink.SinkRecord;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static java.util.Collections.emptyMap;
//import static java.util.Collections.singletonList;

/**
 * Testing the impact of GC on simple connectors:
 * <p />
 * Use Java 11, and run a worker with:
 * <p />
 * <pre>
 * KAFKA_JVM_PERFORMANCE_OPTS=" " KAFKA_HEAP_OPTS="-Xmx4G" KAFKA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC" ./bin/connect-distributed.sh ./config/connect-distributed.properties
 * </pre>
 */
public class NullSinkConnector {
//public class NullSinkConnector extends SinkConnector {
//
//    @Override
//    public void start(Map<String, String> props) {
//    }
//
//    @Override
//    public Class<? extends Task> taskClass() {
//        return NullSinkTask.class;
//    }
//
//    @Override
//    public List<Map<String, String>> taskConfigs(int maxTasks) {
//        return new ArrayList<>(singletonList(emptyMap()));
//    }
//
//    @Override
//    public void stop() {
//
//    }
//
//    @Override
//    public ConfigDef config() {
//        return new ConfigDef();
//    }
//
//    @Override
//    public String version() {
//        return "1.0.0";
//    }
//
//    public static void main(String[] args) throws Exception {
//        NullSinkConnector connector = new NullSinkConnector();
//        NullSinkTask task = (NullSinkTask) connector.taskClass().newInstance();
//        task.start(emptyMap());
//        task.put(new SinkRecord("topic", 0, null, null, Schema.BYTES_SCHEMA, "hello".getBytes(), 1));
//        task.stop();
//    }
}
