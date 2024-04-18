package ru.practicum.main.server.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectValueException extends ResponseStatusException {

    public IncorrectValueException(HttpStatus badRequest, final String message) {
        super(badRequest, message);
    }
}
