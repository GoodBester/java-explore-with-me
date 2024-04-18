package ru.practicum.main.server.error.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException {

    public ValidationException(HttpStatus badRequest, final String message) {
        super();
    }
}
