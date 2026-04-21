package com.bookstore.dto.Auth;

public record TokenRefreshResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {}
