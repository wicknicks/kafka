package org.apache.kafka.connect.handlers;

import java.util.Map;

/**
 * Global static context for a Connect Task
 */
public interface GlobalContext {

    /**
     * @return the configuration of the connector
     */
    Map<String, Object> connectorConfig();

    /**
     * @return the configuration of the Connect worker
     */
    Map<String, Object> workerConfig();

}
