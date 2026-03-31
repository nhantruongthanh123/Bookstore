package com.bookstore.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Length(min = 8, max = 25)
        String password
) {}
