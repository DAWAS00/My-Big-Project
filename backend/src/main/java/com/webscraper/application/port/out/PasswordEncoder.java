package com.webscraper.application.port.out;

/**
 * Port for password hashing operations.
 * Separates security concern from domain.
 */
public interface PasswordEncoder {
    
    String encode(String rawPassword);
    
    boolean matches(String rawPassword, String encodedPassword);
}
