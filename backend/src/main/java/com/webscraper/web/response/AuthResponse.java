package com.webscraper.web.response;

public record AuthResponse(
    String token,
    UserResponse user
) {}
