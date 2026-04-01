package com.bookstore.service.auth;


import com.bookstore.dto.Auth.*;
import com.bookstore.entity.RefreshToken;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpirationMs;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        User newUser = userService.createUser(registerRequest);
        return generateAuthResponse(newUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail(),
                        loginRequest.password()
                )
        );

        User user;
        if (loginRequest.usernameOrEmail().contains("@")) {
            user = userService.findByEmail(loginRequest.usernameOrEmail());
        } else {
            user = userService.findByUsername(loginRequest.usernameOrEmail());
        }
        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = buildUserDetails(user);
                    String accessToken = jwtUtil.generateAccessToken(userDetails);

                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

                    return new TokenRefreshResponse(
                            accessToken,
                            newRefreshToken.getToken(),
                            "Bearer",
                            accessTokenExpirationMs / 1000
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = buildUserDetails(user);

        String accessToken = jwtUtil.generateAccessToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                accessTokenExpirationMs / 1000,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNames
        );
    }

    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName()))
                        .collect(Collectors.toList())
        );
    }
}
