package com.bookstore.service.user;

import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.entity.User;

public interface UserService {
    User createUser(RegisterRequest registerRequest);

    User findByUsername(String username);
}
