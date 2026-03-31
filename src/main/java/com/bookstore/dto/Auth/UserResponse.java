package com.bookstore.dto.Auth;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String phoneNumber,
        Set<String> roles,
        Boolean enabled,
        Boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
