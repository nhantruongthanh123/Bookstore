package com.bookstore.dto.Auth;

import java.util.Set;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String type,
        Long expiresIn,
        Long id,
        String username,
        String email,
        Set<String> roles
) {
    public AuthResponse(String accessToken, String refreshToken, String type, Long expiresIn, Long id, String username, String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.type = type != null ? type : "Bearer";
        this.expiresIn = expiresIn;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
