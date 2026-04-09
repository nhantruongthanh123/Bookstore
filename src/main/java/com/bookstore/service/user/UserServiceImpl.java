package com.bookstore.service.user;

import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.dto.Auth.UpdateUserRequest;
import com.bookstore.dto.Auth.UserResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return mapToUserResponse(user);
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
        return mapToUserResponse(updatedUser);
    }

    private User getUserFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        } else {
            return findByUsername(userDetails.getUsername());
        }
    }

    private UserResponse mapToUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roleNames,
                user.getEnabled(),
                user.getAccountNonLocked(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAvatar(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getGender()
        );
    }

    @Override
    @Transactional
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
