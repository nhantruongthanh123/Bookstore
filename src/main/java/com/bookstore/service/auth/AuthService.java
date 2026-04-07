package com.bookstore.service.auth;

import com.bookstore.dto.Auth.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    UserResponse getCurrentUser(UserDetails userDetails);
}
