package org.apache.kafka.connect.handlers;

/**
 * A logical stage in a Connect pipeline
 */
public enum Stage {

    /**
     * when deserializing messages from Kafka into ConnectRecords
     */
    DESERIALIZATION,

    /**
     * when running any transform operation on a record
     */
    TRANSFORMATION,

    /**
     * when calling the poll() method on a SourceConnector
     */
    TASK_POLL,

    /**
     * when calling the put() method on a SinkConnector
     */
    TASK_PUT,

    /**
     * when serializing ConnectRecords to be produced into Kafka
     */
    SERIALIZATION

}
