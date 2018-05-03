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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.handlers.ErrorHandler;
import org.apache.kafka.connect.handlers.ErrorHandlerResponse;
import org.apache.kafka.connect.handlers.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogAndFailHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(LogAndFailHandler.class);

    private final ConfigDef configDef = new ConfigDef();

    @Override
    public void configure(Map<String, ?> configs) {
    }

    @Override
    public void init() {
    }

    @Override
    public ConfigDef config() {
        return configDef;
    }

    @Override
    public ErrorHandlerResponse onError(ProcessingContext context) {
        log.info("Task failure. context={}", context);
        return ErrorHandlerResponse.FAIL;
    }

    @Override
    public void close() {

    }
}
