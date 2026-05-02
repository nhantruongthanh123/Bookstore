package com.bookstore.service.user;

import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.dto.Auth.UpdateUserRequest;
import com.bookstore.dto.Auth.UserResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    User createUser(RegisterRequest registerRequest);

    User findByUsername(String username);
    User findByEmail(String email);
    
    UserResponse getCurrentUser(UserDetails userDetails);
    UserResponse updateUser(UserDetails userDetails, UpdateUserRequest request);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);
}
