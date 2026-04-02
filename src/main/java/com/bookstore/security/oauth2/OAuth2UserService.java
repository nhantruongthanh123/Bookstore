package com.bookstore.security.oauth2;

import com.bookstore.dto.Auth.OAuth2UserInfo;
import com.bookstore.entity.User;

public interface OAuth2UserService {
    User processOAuth2User(String provider, OAuth2UserInfo userInfo);
}
