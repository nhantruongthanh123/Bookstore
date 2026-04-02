package com.bookstore.security.oauth2;

import com.bookstore.dto.Auth.OAuth2UserInfo;
import com.bookstore.entity.User;
import com.bookstore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final OAuth2UserService oauth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {

            if (ex instanceof OAuth2AuthenticationException) {
                throw (OAuth2AuthenticationException) ex;
            }

            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo userInfo = OAuth2UserInfo.fromAttributes(oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        User user = oauth2UserService.processOAuth2User(provider, userInfo);

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}
