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
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.handlers.ErrorHandler;
import org.apache.kafka.connect.handlers.ErrorHandlerResponse;
import org.apache.kafka.connect.handlers.ProcessingContext;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RetryNTimesHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RetryNTimesHandler.class);

    private RetryNTimesHandlerConfig config;
    private Time time;

    public RetryNTimesHandler() {
        time = new SystemTime();
    }

    RetryNTimesHandler(Time time) {
        this.time = time;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        config = new RetryNTimesHandlerConfig(configs);
    }

    @Override
    public void init() {
    }

    @Override
    public ConfigDef config() {
        return RetryNTimesHandlerConfig.CONFIG;
    }

    @Override
    public ErrorHandlerResponse onError(ProcessingContext context) {
        if (context.attempt() > config.maxAttempts()) {
            return ErrorHandlerResponse.FAIL;
        }

        // wait
        int delay;
        if (config.isSleepEnabled()) {
            delay = config.minDelay() << (context.attempt() - 1);
            if (delay < config.maxDelay()) {
                time.sleep(delay);
            } else {
                delay = ThreadLocalRandom.current().nextInt(config.minDelay(), delay);
            }
            log.info("ErrorHandler called with context={}. Sleeping for {} millis.", context, delay);
            time.sleep(delay);
        }

        // return retry
        return ErrorHandlerResponse.RETRY;
    }

    @Override
    public void close() {

    }

    public static void main(String[] args) {
        Map<String, String> confMap = new HashMap<>();
        confMap.put("name", "dummy");
        confMap.put("tasks.max", "1");
        confMap.put("connector.class", "xyz");
        confMap.put("error_handling.strategy.class", "org.apache.kafka.connect.runtime.handlers.RetryNTimesHandler");
        confMap.put("error_handling.attempts.max", "10");

        ConnectorConfig connectorConfig = new ConnectorConfig(null, confMap);
        RetryNTimesHandler handler = new RetryNTimesHandler();

        handler.configure(connectorConfig.originalsWithPrefix("error_handling."));
        handler.init();
        for (int i=0; i<10; i++) {
            handler.onError(new ProcessingContextImpl(i + 1));
        }
    }

}
