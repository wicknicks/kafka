package org.apache.kafka.connect.runtime.handlers;

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
