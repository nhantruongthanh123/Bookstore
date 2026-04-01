package com.bookstore.dto.Auth;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {}
