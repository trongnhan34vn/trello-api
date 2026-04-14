package com.nhantic.trelloapi.exception;

public class InternalServerErrorException extends ApplicationException {

    private static final int status = 500;

    public InternalServerErrorException(String code, String message) {
        super(status, code, message);
    }

    public InternalServerErrorException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
