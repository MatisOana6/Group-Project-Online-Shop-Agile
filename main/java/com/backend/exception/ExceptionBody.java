package com.backend.exception;

import lombok.Getter;

@Getter
public class ExceptionBody {
    private final String message;

    public ExceptionBody(String message) {
        this.message = message;
    }
}
