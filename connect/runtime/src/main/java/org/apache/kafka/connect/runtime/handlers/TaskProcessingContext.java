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

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.handlers.ProcessingContext;
import org.apache.kafka.connect.handlers.Stage;
import org.apache.kafka.connect.runtime.WorkerConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TaskProcessingContext implements ProcessingContext {

    private final List<Stage> stages;
    private final Map<String, Object> workerConfig;

    private int attempt = 1;
    private int index = 0;

    public static Builder newBuilder(WorkerConfig config) {
        return new Builder(config.originals());
    }

    private TaskProcessingContext(Map<String, Object> workerConfig, List<Stage> stages) {
        this.stages = stages;
        this.workerConfig = workerConfig;
    }

    public void markAsFailed() {
        attempt++;
    }

    public void markAsSuccess() {
        attempt = 1;
        index++;
    }

    @Override
    public Map<String, Object> workerConfig() {
        return workerConfig;
    }

    @Override
    public List<Stage> stages() {
        return stages;
    }

    @Override
    public String taskId() {
        return null;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int attempt() {
        return attempt;
    }

    @Override
    public long timeOfError() {
        return 0;
    }

    @Override
    public Exception exception() {
        return null;
    }

    @Override
    public ConnectRecord record() {
        return null;
    }

    @Override
    public Struct toStruct() {
        SchemaBuilder builder = new SchemaBuilder(Schema.Type.STRUCT).field("attempt", SchemaBuilder.int32());
        Struct struct = new Struct(builder.build());
        struct.put("attempt", attempt());
        return struct;
    }

    public static class Builder {

        private final Map<String, Object> workerConfig;
        LinkedList<Stage> stages = new LinkedList<>();

        private Builder(Map<String, Object> workerConfig) {
            this.workerConfig = workerConfig;
        }

        public void prependStage(StageBuilder builder) {
            stages.addFirst(builder.build());
        }

        public void appendStage(StageBuilder builder) {
            stages.addLast(builder.build());
        }

        public TaskProcessingContext build() {
            return new TaskProcessingContext(workerConfig, stages);
        }
    }
}
