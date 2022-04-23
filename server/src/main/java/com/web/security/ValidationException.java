package com.web.security;

/**
 * Exception thrown in controllers when some validation or assertion fails.
 */
public class ValidationException extends RuntimeException
{
    public ValidationException(String message)
    {
        super(message);
    }
}
