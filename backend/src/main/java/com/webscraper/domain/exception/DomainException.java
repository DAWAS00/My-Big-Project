package com.webscraper.domain.exception;

/**
 * Base exception for domain layer errors.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
