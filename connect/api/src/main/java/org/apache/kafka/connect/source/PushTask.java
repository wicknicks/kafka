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
package org.apache.kafka.connect.source;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.List;
import java.util.function.Consumer;

public abstract class PushTask extends SourceTask {

    public void producer(Consumer<SourceRecord> writer) {

    }

    @Override
    public final List<SourceRecord> poll() throws InterruptedException {
        return null;
    }

    public final void commitRecord(SourceRecord record) throws InterruptedException {

    }

    public final void commitRecord(SourceRecord record, RecordMetadata metadata)
            throws InterruptedException {
        // by default, just call other method for backwards compatability
        commitRecord(record);
    }

    public final void commit() throws InterruptedException {
        // This space intentionally left blank.
    }

}
