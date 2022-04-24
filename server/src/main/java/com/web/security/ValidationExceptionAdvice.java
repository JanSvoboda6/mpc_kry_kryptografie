package com.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ValidationControllerAdvice picks a thrown {@link ValidationException} and creates a response.
 */
@ControllerAdvice
public class ValidationExceptionAdvice
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionAdvice.class);

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(ValidationException exception)
    {
        LOGGER.info("Validation exception: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
