package com.nhantic.trelloapi.exception;

public class ConflictException extends ApplicationException {

    private static final int status = 409;

    public ConflictException(String code, String message) {
        super(status, code, message);
    }

    public ConflictException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}