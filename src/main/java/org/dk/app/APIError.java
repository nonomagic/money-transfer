package org.dk.app;

public class APIError extends RuntimeException {
    private int errorCode;

    public APIError(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
