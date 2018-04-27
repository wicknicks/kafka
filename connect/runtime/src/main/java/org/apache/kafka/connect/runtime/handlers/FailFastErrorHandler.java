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

import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.handlers.ErrorHandler;
import org.apache.kafka.connect.handlers.ErrorHandlerResponse;
import org.apache.kafka.connect.handlers.GlobalContext;
import org.apache.kafka.connect.handlers.ProcessingContext;

import java.util.Map;

public class FailFastErrorHandler implements ErrorHandler {

    private GlobalContext globalContext;
    private Map<String, Object> handlerConfig;

    @Override
    public void init(GlobalContext context, Map<String, Object> handlerConfig) {
        this.globalContext = context;
        this.handlerConfig = handlerConfig;
    }

    @Override
    public ErrorHandlerResponse onError(ProcessingContext context, Exception exception, SchemaAndValue key, SchemaAndValue value) {
        return ErrorHandlerResponse.FAIL;
    }

    @Override
    public void stop() {

    }

    @Override
    public String toString() {
        return "FailFastErrorHandler{" +
                "globalContext=" + globalContext +
                ", handlerConfig=" + handlerConfig +
                '}';
    }
}
