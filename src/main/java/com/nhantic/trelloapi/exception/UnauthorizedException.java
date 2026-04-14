package com.nhantic.trelloapi.exception;

public class UnauthorizedException extends ApplicationException {

    private static final int status = 401;

    public UnauthorizedException(String code, String message) {
        super(status, code, message);
    }

    public UnauthorizedException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
