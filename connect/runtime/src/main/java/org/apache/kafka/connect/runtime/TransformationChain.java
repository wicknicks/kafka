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
package org.apache.kafka.connect.runtime;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.handlers.ErrorHandler;
import org.apache.kafka.connect.handlers.ProcessingContext;
import org.apache.kafka.connect.runtime.handlers.Retry;
import org.apache.kafka.connect.runtime.handlers.LogAndFailHandler;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TransformationChain<R extends ConnectRecord<R>> {

    private final List<Transformation<R>> transformations;
    private final ErrorHandler errorHandler;
    private final Retry retry;

    public TransformationChain(List<Transformation<R>> transformations, ErrorHandler errorHandler, Retry retry) {
        this.transformations = transformations;
        this.errorHandler = errorHandler;
        this.retry = retry;
    }

    public R apply(R record) {
        if (transformations.isEmpty()) return record;

        for (Transformation<R> transformation : transformations) {
            boolean failed = true;
            while (failed) {
                try {
                    record = transformation.apply(record);
                    failed = false;
                    if (record == null) break;
                } catch (Exception e) {
                    ProcessingContext p = null;
                    switch (errorHandler.onError(p, e,
                            new SchemaAndValue(record.keySchema(), record.key()),
                            new SchemaAndValue(record.valueSchema(), record.value()))) {
                        case FAIL: throw new ConnectException(e);
                        case SKIP: return null;
                        case RETRY:
                            retry.sleep();
                            break;
                        default: throw new ConnectException("Unknown error handler response");
                    }
                }
            }
        }

        return record;
    }

    public void close() {
        for (Transformation<R> transformation : transformations) {
            transformation.close();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformationChain that = (TransformationChain) o;
        return Objects.equals(transformations, that.transformations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transformations);
    }

    public static <R extends ConnectRecord<R>> TransformationChain<R> noOp() {
        return new TransformationChain<R>(Collections.<Transformation<R>>emptyList(),
                new LogAndFailHandler(), Retry.SLEEPING_WAIT);
    }

}
