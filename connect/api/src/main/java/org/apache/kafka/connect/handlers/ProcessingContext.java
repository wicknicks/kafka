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
package org.apache.kafka.connect.handlers;

import org.apache.kafka.connect.connector.ConnectRecord;

import java.util.Map;

/**
 * This object will contain all the runtime context for an error which occurs in the Connect framework while
 * processing a record.
 */
public interface ProcessingContext {

    /**
     * @return which task reported this error
     */
    String taskId();

    /**
     * @return at what stage in processing did the error happen
     */
    Stage stageType();

    /**
     * @return description of the stage (for example, the class, position in transform chain (if applicable))
     */
    Map<String, Object> stageDescription();

    /**
     * @return properties used to configure this stage
     */
    Map<String, Object> stageProperties();

    /**
     * @return which attempt was this (first error will be 0)
     */
    int attempt();

    /**
     * @return the original record which was sent to the first stage of processing
     */
    ConnectRecord originalRecord();
}
