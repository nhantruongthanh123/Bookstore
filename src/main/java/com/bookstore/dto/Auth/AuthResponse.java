package com.bookstore.dto.Auth;

import java.util.Set;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String username,
        String email,
        Set<String> roles
) {
    public AuthResponse(String token, String type, Long id, String username, String email, Set<String> roles) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
