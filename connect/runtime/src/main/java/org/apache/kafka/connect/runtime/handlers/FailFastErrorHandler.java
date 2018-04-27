package org.apache.kafka.connect.runtime.handlers;

import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.handlers.ErrorHandler;
import org.apache.kafka.connect.handlers.ErrorHandlerResponse;
import org.apache.kafka.connect.handlers.ProcessingContext;

import java.util.Map;

public class FailFastErrorHandler implements ErrorHandler {

    private Map<String, Object> connectorConfig;
    private Map<String, Object> workerConfig;
    private Map<String, Object> handlerConfig;

    @Override
    public void init(Map<String, Object> connectorConfig, Map<String, Object> workerConfig, Map<String, Object> handlerConfig) {
        this.connectorConfig = connectorConfig;
        this.workerConfig = workerConfig;
        this.handlerConfig = handlerConfig;
    }

    @Override
    public ErrorHandlerResponse onError(ProcessingContext context, Exception exception, SchemaAndValue key, SchemaAndValue value) {
        return ErrorHandlerResponse.FAIL;
    }

    @Override
    public void close() {

    }

}
