package org.apache.kafka.connect.handlers;

import org.apache.kafka.connect.data.SchemaAndValue;

import java.util.Map;

public interface ErrorHandler {

    /**
     * Initialize the handler with connector, worker and handler config. The connector and worker configs are only
     * used for reporting purposes. the handler config is used to configure this instance of the handler.
     *
     * @param connectorConfig the connector config
     * @param workerConfig the worker config
     * @param handlerConfig the properties used to configure this handler
     */
    void init(Map<String, Object> connectorConfig, Map<String, Object> workerConfig, Map<String, Object> handlerConfig);

    /**
     * This method is called for any error which occurs during the processing of a record in a Connect task.
     *
     * @param context the processing context
     * @param exception the Exception
     * @param key the key of the record (might be null)
     * @param value the value of the record (might be null if the error occurs during SourceTask#poll, for example).
     * @return a directive on how to handle this error.
     */
    ErrorHandlerResponse onError(ProcessingContext context, Exception exception, SchemaAndValue key, SchemaAndValue value);

    /**
     * Flush any outstanding data, and close this handler.
     */
    void close();

}
