package com.nhantic.trelloapi.exception;

public class BadRequestException extends ApplicationException{

    private static final int status = 400;

    public BadRequestException(String code, String message) {
        super(status, code, message);
    }

    public BadRequestException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
