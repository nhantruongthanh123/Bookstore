package com.bookstore.controller;

import com.bookstore.dto.Auth.UpdateUserRequest;
import com.bookstore.dto.Auth.UserResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse userResponse = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse userResponse = userService.updateUser(userDetails, request);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        PageResponse<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
}

