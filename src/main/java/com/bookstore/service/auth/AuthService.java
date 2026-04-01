package com.bookstore.service.auth;

import com.bookstore.dto.Auth.*;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}
