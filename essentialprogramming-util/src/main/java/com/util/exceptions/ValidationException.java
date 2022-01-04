package com.util.exceptions;

import com.util.enums.HTTPCustomStatus;

public class ValidationException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private final String detail;

    public String getDetail() {
        return detail;
    }

    public ValidationException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public ValidationException(String message, Throwable cause, String detail) {
        super(message, cause);
        this.detail = detail;
    }

}
