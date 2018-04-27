package org.apache.kafka.connect.handlers;

/**
 * A directive from the error handler to the connect framework on how to handle a given error.
 */
public enum ErrorHandlerResponse {

    RETRY(1),

    SKIP(2),

    FAIL(3);

    private final int id;

    ErrorHandlerResponse(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
