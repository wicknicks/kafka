package org.apache.kafka.connect.handlers;

import java.util.Map;

/**
 * This object will contain all the runtime context for an error which occurs in the Connect framework while
 * processing a record.
 */
public interface ProcessingContext {

    /**
     * @return which task reported this error
     */
    String taskId();

    /**
     * @return at what stage in processing did the error happen
     */
    Stage stageType();

    /**
     * @return description of the stage (for example, the class, position in transform chain (if applicable))
     */
    Map<String, Object> stageDescription();

    /**
     * @return properties used to configure this stage
     */
    Map<String, Object> stageProperties();

    /**
     * @return which attempt was this (first error will be 0)
     */
    int attempt();


}
