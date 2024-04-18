package ru.practicum.main.server.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.main.server.error.exception.IncorrectValueException;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.error.exception.ValidationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundRequest(final NotFoundException e) {
        return new ApiError("NOT_FOUND", "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(IncorrectValueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectValue(final IncorrectValueException e) {
        return new ApiError("CONFLICT", "The value does not comply with requirements", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final Exception e) {

        return new ApiError("BAD_REQUEST", "BAD_REQUEST",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Throwable e) {

        return new ApiError("SERVER_ERROR", "Internal Server Error.",
                e.getMessage(), LocalDateTime.now());
    }

}
