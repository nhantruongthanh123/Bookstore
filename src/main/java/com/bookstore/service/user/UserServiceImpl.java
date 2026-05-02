package com.bookstore.service.user;

import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.dto.Auth.UpdateUserRequest;
import com.bookstore.dto.Auth.UserResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new DuplicateResourceException("Username is already in use");
        }
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new DuplicateResourceException("Email is already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role is not found"));

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        User newUser = new User();
        newUser.setUsername(registerRequest.username());
        newUser.setEmail(registerRequest.email());
        newUser.setPassword(passwordEncoder.encode(registerRequest.password())); // Băm pass tại đây!
        newUser.setFullName(registerRequest.fullName());
        newUser.setPhoneNumber(registerRequest.phoneNumber());
        newUser.setRoles(userRoles);

        return userRepository.save(newUser);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserResponse getCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        User user = getUserFromUserDetails(userDetails);
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserDetails userDetails, UpdateUserRequest request) {
        if (userDetails == null) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        User user = getUserFromUserDetails(userDetails);

        // Update only provided fields
        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }
        if (request.address() != null) {
            user.setAddress(request.address());
        }
        if (request.dateOfBirth() != null) {
            user.setDateOfBirth(request.dateOfBirth());
        }
        if (request.gender() != null) {
            user.setGender(request.gender());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    private User getUserFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal userPrincipal) {
            if (userPrincipal.getUser() != null) {
                return userPrincipal.getUser();
            }
        }
        return findByUsername(userDetails.getUsername());
    }

    @Override
    @Transactional
    public PageResponse<UserResponse> getAllUsers(Pageable pageable){
        Page<UserResponse> users = userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
        return PageResponse.of(users);
    }
}
