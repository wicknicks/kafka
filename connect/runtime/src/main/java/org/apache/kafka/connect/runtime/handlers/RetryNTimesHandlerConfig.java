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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class RetryNTimesHandlerConfig extends AbstractConfig {

    private static final String MAX_ATTEMPTS = "attempts.max";
    private static final int MAX_ATTEMPTS_DEFAULT = 5;
    private static final String MAX_ATTEMPTS_DOC = "the maximum number of times we want to reattempt an operation.";

    private static final String SLEEP_ON_ERROR = "sleep.enable";
    private static final boolean SLEEP_ON_ERROR_DEFAULT = true;
    private static final String SLEEP_ON_ERROR_DOC = "if false, the call to onError will immediately return.";

    private static final String INIT_DELAY = "delay.min.millis";
    private static final int INIT_DELAY_DEFAULT = 50;
    private static final String INIT_DELAY_DOC = "amount of time to wait for the first failure";

    private static final String FINAL_DELAY = "delay.max.millis";
    private static final int FINAL_DELAY_DEFAULT = 60000;
    private static final String FINAL_DELAY_DOC = "amount of time to wait for the final failure";

    protected static ConfigDef CONFIG = new ConfigDef()
                .define(MAX_ATTEMPTS, ConfigDef.Type.INT, MAX_ATTEMPTS_DEFAULT,
                        ConfigDef.Range.atLeast(0), ConfigDef.Importance.MEDIUM, MAX_ATTEMPTS_DOC)
                .define(SLEEP_ON_ERROR, ConfigDef.Type.BOOLEAN, SLEEP_ON_ERROR_DEFAULT,
                        ConfigDef.Importance.LOW, SLEEP_ON_ERROR_DOC)
                .define(INIT_DELAY, ConfigDef.Type.INT, INIT_DELAY_DEFAULT,
                        ConfigDef.Range.atLeast(0), ConfigDef.Importance.MEDIUM, INIT_DELAY_DOC)
                .define(FINAL_DELAY, ConfigDef.Type.INT, FINAL_DELAY_DEFAULT,
                        ConfigDef.Range.atLeast(0), ConfigDef.Importance.MEDIUM, FINAL_DELAY_DOC);

    public RetryNTimesHandlerConfig(Map<?, ?> originals) {
        super(CONFIG, originals);
    }

    public RetryNTimesHandlerConfig(ConfigDef configDef, Map<?, ?> originals) {
        super(configDef, originals);
    }

    public int maxAttempts() {
        return getInt(MAX_ATTEMPTS);
    }

    public boolean isSleepEnabled() {
        return getBoolean(SLEEP_ON_ERROR);
    }

    public int minDelay() {
        return getInt(INIT_DELAY);
    }

    public int maxDelay() {
        return getInt(FINAL_DELAY);
    }
}
