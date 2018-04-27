package org.apache.kafka.connect.handlers;

/**
 * A directive from the error handler to the connect framework on how to handle a given error.
 */
public enum ErrorHandlerResponse {

    /**
     * retry the previous operation
     */
    RETRY(1),

    /**
     * drop the record and move to the next one
     */
    SKIP(2),

    /**
     * throw an Exception, and kill the task
     */
    FAIL(3);

    private final int id;

    ErrorHandlerResponse(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
