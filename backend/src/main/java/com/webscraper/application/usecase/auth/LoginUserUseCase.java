package com.webscraper.application.usecase.auth;

import com.webscraper.application.port.out.PasswordEncoder;
import com.webscraper.application.port.out.UserRepository;
import com.webscraper.domain.entity.User;
import com.webscraper.domain.exception.DomainException;

/**
 * Use case: Login user.
 */
public class LoginUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record Command(String email, String password) {}
    
    public record Result(User user) {}

    public Result execute(Command command) {
        // Find user
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new DomainException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new DomainException("Invalid email or password");
        }

        // Check active
        if (!user.isActive()) {
            throw new DomainException("Account is disabled");
        }

        return new Result(user);
    }
}
