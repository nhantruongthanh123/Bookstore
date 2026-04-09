package com.bookstore.mapper;

import com.bookstore.dto.Auth.UserResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    default String mapRoleToString(Role role) {
        if (role == null) {
            return null;
        }
        return role.getName();
    }
}
