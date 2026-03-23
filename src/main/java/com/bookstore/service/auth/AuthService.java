package com.bookstore.service.auth;

import com.bookstore.dto.Auth.AuthResponse;
import com.bookstore.dto.Auth.LoginRequest;
import com.bookstore.dto.Auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
