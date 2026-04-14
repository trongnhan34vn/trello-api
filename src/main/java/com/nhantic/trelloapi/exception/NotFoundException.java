package com.nhantic.trelloapi.exception;

public class NotFoundException extends ApplicationException {

    private static final int status = 404;

    public NotFoundException(String code, String message) {
        super(status, code, message);
    }

    public NotFoundException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
