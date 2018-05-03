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
import org.apache.kafka.connect.data.Struct;

import java.util.List;
import java.util.Map;

/**
 * This object will contain all the runtime context for an error which occurs in the Connect framework while
 * processing a record.
 */
public interface ProcessingContext {

    /**
     * @return the configuration of the Connect worker
     */
    Map<String, Object> workerConfig();

    /**
     * @return which task reported this error
     */
    String taskId();

    /**
     * @return an ordered list of stages. Connect will start with executing stage 0 and then move up the list.
     */
    List<Stage> stages();

    /**
     * @return at what stage did this operation fail (0 indicates first stage)
     */
    int index();

    /**
     * @return which attempt was this (first error will be 0)
     */
    int attempt();

    /**
     * @return the (epoch) time of failure
     */
    long timeOfError();

    /**
     * The exception accompanying this failure (if any)
     */
    Exception exception();

    /**
     * @return the record which when input the current stage caused the failure.
     */
    ConnectRecord record();

    /**
     * create a {@link Struct} from the various parameters in this Context object.
     */
    Struct toStruct();
}
