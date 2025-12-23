package com.webscraper.domain.exception;

/**
 * Exception for access denied scenarios.
 */
public class AccessDeniedException extends DomainException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
}
