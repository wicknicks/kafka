package org.apache.kafka.connect.handlers;

import java.util.Properties;

/**
 * This object will contain all the runtime context for an error which occurs in the Connect framework while
 * processing a record.
 */
public interface ProcessingContext {

    String taskId();

    Stage stageType();

    String stageName();

    Properties stageProperties();

    int attempt();

}
