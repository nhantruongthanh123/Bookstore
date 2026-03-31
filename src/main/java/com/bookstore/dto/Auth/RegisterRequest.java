package com.bookstore.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Length(min = 8, max = 25)
        String password,

        @Email(message = "Email is wrong")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Name is required")
        String fullName,

        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {}
