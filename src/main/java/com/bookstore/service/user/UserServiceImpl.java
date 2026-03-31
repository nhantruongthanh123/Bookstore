package com.bookstore.service.user;

import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

}
