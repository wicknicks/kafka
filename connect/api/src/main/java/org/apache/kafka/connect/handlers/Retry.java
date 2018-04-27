package org.apache.kafka.connect.handlers;

/**
 * What to do in between retries.
 */
public enum Retry {

    SLEEPING_WAIT {
        @Override
        public void sleep() {

        }
    };

    public abstract void sleep();

}
