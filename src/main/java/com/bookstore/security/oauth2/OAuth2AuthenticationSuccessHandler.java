package com.bookstore.security.oauth2;

import com.bookstore.entity.User;
import com.bookstore.security.JwtUtil;
import com.bookstore.security.UserPrincipal;
import com.bookstore.service.auth.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NullMarked
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String authorizedRedirectUri;

    @Value("${jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new IllegalStateException("Can't authenticate user!");
        }

        User user = userPrincipal.getUser();
        if (user == null) {
            throw new IllegalStateException("OAuth2 principal is missing user entity");
        }

        String accessToken = jwtUtil.generateAccessToken(userPrincipal);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        CookieUtils.addCookie(response, "refreshToken", refreshToken, refreshTokenExpiration);

        String targetUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
