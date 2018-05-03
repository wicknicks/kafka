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

import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.SchemaAndValue;

import java.io.Closeable;
import java.util.Map;

public interface ErrorHandler extends Configurable, Closeable {

    /**
     * Initialize the handler with connector, worker and handler config. The connector and worker configs are only
     * used for reporting purposes. the handler config is used to configure this instance of the handler.
     */
    void init();

    /**
     * @return the ConfigDef for this handler
     */
    ConfigDef config();

    /**
     * This method is called for any error which occurs during the processing of a record in a Connect task.
     *
     * @param context the processing context
     * @return a directive on how to handle this error.
     */
    ErrorHandlerResponse onError(ProcessingContext context);

    /**
     * Flush any outstanding data, and close this handler.
     */
    @Override
    void close();
}
