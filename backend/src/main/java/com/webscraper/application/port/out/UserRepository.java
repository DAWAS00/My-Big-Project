package com.webscraper.application.port.out;

import com.webscraper.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for user persistence operations.
 * Implemented by infrastructure layer.
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(UUID id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
