package com.bookstore.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        @Size(max = 15, message = "Phone number must not exceed 15 characters")
        String phoneNumber,

        @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
        String avatar,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        LocalDate dateOfBirth,

        @Size(max = 20, message = "Gender must not exceed 20 characters")
        String gender
) {}
