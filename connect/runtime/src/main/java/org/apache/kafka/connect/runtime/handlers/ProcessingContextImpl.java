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

import java.util.List;
import java.util.Map;

public class ProcessingContextImpl implements ProcessingContext {

    private final int attempt;

    public ProcessingContextImpl(int attempt) {
        this.attempt = attempt;
    }

    @Override
    public Map<String, Object> connectorConfig() {
        return null;
    }

    @Override
    public Map<String, Object> workerConfig() {
        return null;
    }

    @Override
    public List<Stage> stages() {
        return null;
    }

    @Override
    public String taskId() {
        return null;
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public int attempt() {
        return attempt;
    }

    @Override
    public long timeOfFailure() {
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
}
