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

import org.apache.kafka.connect.handlers.Stage;
import org.apache.kafka.connect.handlers.StageType;

import java.util.Map;
import java.util.Objects;

public class StageBuilder {

    private final StageType type;
    private Map<String, Object> originals;
    private Class<? extends Object> klass;

    public StageBuilder(StageType type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    public StageBuilder setConfig(Map<String, Object> originals) {
        Objects.requireNonNull(originals);
        this.originals = originals;
        return this;
    }

    public StageBuilder setClass(Class<?> klass) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        return this;
    }

    public Stage build() {
        return new StageImpl(type, originals, klass);
    }

    private static class StageImpl implements Stage {

        private final StageType type;
        private final Map<String, Object> originals;
        private final Class<?> klass;

        public StageImpl(StageType type, Map<String, Object> originals, Class<?> klass) {
            this.type = type;
            this.originals = originals;
            this.klass = klass;
        }

        @Override
        public StageType type() {
            return type;
        }

        @Override
        public Class<?> executingClass() {
            return klass;
        }

        @Override
        public Map<String, Object> config() {
            return originals;
        }
    }
}
