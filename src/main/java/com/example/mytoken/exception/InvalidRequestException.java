package com.example.mytoken.exception;

import org.springframework.validation.BindingResult;

public class InvalidRequestException extends RuntimeException {

    private BindingResult errors;

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, BindingResult errors) {
        super(message);
        this.errors = errors;
    }

    public BindingResult getErrors() {
        return errors;
    }
}
