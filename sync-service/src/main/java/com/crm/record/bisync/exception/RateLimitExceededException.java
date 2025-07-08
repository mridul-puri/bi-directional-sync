package com.crm.record.bisync.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super();
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
