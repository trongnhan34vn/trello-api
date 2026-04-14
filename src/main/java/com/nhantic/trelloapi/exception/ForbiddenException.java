package com.nhantic.trelloapi.exception;

public class ForbiddenException extends ApplicationException {

    private static final int status = 403;

    public ForbiddenException(String code, String message) {
        super(status, code, message);
    }

    public ForbiddenException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
