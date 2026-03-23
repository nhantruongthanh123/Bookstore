package com.bookstore.service.auth;


import com.bookstore.dto.Auth.AuthResponse;
import com.bookstore.dto.Auth.LoginRequest;
import com.bookstore.dto.Auth.RegisterRequest;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already in use");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role is not found"));

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFullName(registerRequest.getFullName());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setRoles(userRoles);

        userRepository.save(newUser);

        Set<String> roleNames = newUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        var userDetails = new org.springframework.security.core.userdetails.User(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList())
        );

        String jwtToken = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwtToken, "Bearer", newUser.getId(), newUser.getUsername(), newUser.getEmail(), roleNames);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername()).
                orElseThrow(() -> new RuntimeException("Username not found"));

        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList())
        );

        String jwtToken = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwtToken, "Bearer", user.getId(), user.getUsername(), user.getEmail(), roleNames);

    }

}
