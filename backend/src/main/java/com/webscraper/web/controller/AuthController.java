package com.webscraper.web.controller;

import com.webscraper.application.usecase.auth.LoginUserUseCase;
import com.webscraper.application.usecase.auth.RegisterUserUseCase;
import com.webscraper.infrastructure.security.JwtTokenProvider;
import com.webscraper.web.request.LoginRequest;
import com.webscraper.web.request.RegisterRequest;
import com.webscraper.web.response.ApiResponse;
import com.webscraper.web.response.AuthResponse;
import com.webscraper.web.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 * Thin controller: validates input, calls use-case, returns response.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            JwtTokenProvider jwtTokenProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserUseCase.Command(
                request.email(),
                request.password(),
                request.fullName()
        );
        
        var result = registerUserUseCase.execute(command);
        
        String token = jwtTokenProvider.generateToken(result.user());
        var response = new AuthResponse(token, UserResponse.from(result.user()));
        
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var command = new LoginUserUseCase.Command(request.email(), request.password());
        var result = loginUserUseCase.execute(command);
        
        String token = jwtTokenProvider.generateToken(result.user());
        var response = new AuthResponse(token, UserResponse.from(result.user()));
        
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
