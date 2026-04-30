package com.bookstore.service.auth;


import com.bookstore.dto.Auth.*;
import com.bookstore.entity.RefreshToken;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.security.JwtUtil;
import com.bookstore.security.UserPrincipal;
import com.bookstore.security.oauth2.CookieUtils;
import com.bookstore.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Integer refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest, HttpServletResponse response) {
        User newUser = userService.createUser(registerRequest);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(newUser.getId());
        CookieUtils.addCookie(response, "refreshToken", refreshToken.getToken(), refreshTokenExpiration);

        return generateAuthResponse(newUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {
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

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        CookieUtils.addCookie(response, "refreshToken", refreshToken.getToken(), refreshTokenExpiration);

        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(String requestRefreshToken, HttpServletResponse response) {
        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing from cookies!");
        }

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = buildUserDetails(user);
                    String accessToken = jwtUtil.generateAccessToken(userDetails);

                    refreshTokenService.deleteByUserId(user.getId());

                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                    CookieUtils.addCookie(response, "refreshToken", newRefreshToken.getToken(), refreshTokenExpiration);

                    return new TokenRefreshResponse(
                            accessToken,
                            "Bearer",
                            accessTokenExpiration / 1000
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response){
        CookieUtils.getCookie(request, "refreshToken")
                .map(jakarta.servlet.http.Cookie::getValue)
                .ifPresent(refreshTokenService::deleteByToken);
        CookieUtils.deleteCookie(response, "refreshToken");
    }

    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = buildUserDetails(user);

        String accessToken = jwtUtil.generateAccessToken(userDetails);

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                "Bearer",
                accessTokenExpiration / 1000,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNames
        );
    }

    private UserDetails buildUserDetails(User user) {
        return new UserPrincipal(user);
    }
}
