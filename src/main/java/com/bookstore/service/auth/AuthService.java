package com.bookstore.service.auth;

import com.bookstore.dto.Auth.*;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest, HttpServletResponse response);
    AuthResponse login(LoginRequest loginRequest, HttpServletResponse response);
    TokenRefreshResponse refreshToken(String request,  HttpServletResponse response);
}
