package com.nhantic.trelloapi.exception;

import lombok.Getter;
import lombok.Setter;

public abstract class ApplicationException extends RuntimeException {
    @Getter
    @Setter
    String code;
    String message;
    @Getter
    @Setter
    int status;

    public ApplicationException(int status, String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public ApplicationException(int status, String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setErrorCode(String errorCode) {
        this.code = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.message = errorMessage;
    }
}
