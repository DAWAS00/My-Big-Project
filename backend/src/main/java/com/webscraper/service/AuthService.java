package com.webscraper.service;

import com.webscraper.dto.request.LoginRequest;
import com.webscraper.dto.request.RegisterRequest;
import com.webscraper.dto.response.AuthResponse;
import com.webscraper.dto.response.UserResponse;
import com.webscraper.entity.User;
import com.webscraper.exception.ResourceNotFoundException;
import com.webscraper.exception.ValidationException;
import com.webscraper.repository.UserRepository;
import com.webscraper.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password");
        }
        
        if (!user.getIsActive()) {
            throw new ValidationException("Account is disabled");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .build();
    }
    
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
