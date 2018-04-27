package org.apache.kafka.connect.handlers;

/**
 * A logical stage in a Connect pipeline
 */
public enum Stage {

    DESERIALIZATION,

    TRANSFORMATION,

    TASK_POLL,

    TASK_PUT,

    SERIALIZATION

}
