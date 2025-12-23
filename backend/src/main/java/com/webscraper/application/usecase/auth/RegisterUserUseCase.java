package com.webscraper.application.usecase.auth;

import com.webscraper.application.port.out.PasswordEncoder;
import com.webscraper.application.port.out.UserRepository;
import com.webscraper.domain.entity.User;
import com.webscraper.domain.exception.DomainException;

/**
 * Use case: Register a new user.
 * Single responsibility: user registration only.
 */
public class RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record Command(String email, String password, String fullName) {}
    
    public record Result(User user) {}

    public Result execute(Command command) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(command.email())) {
            throw new DomainException("Email already exists");
        }

        // Create user with hashed password
        String hashedPassword = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), hashedPassword, command.fullName());

        // Persist
        User saved = userRepository.save(user);
        
        return new Result(saved);
    }
}
