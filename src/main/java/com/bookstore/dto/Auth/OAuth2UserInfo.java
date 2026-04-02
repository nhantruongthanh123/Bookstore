package com.bookstore.dto.Auth;

import java.util.Map;

public record OAuth2UserInfo(
        String id,        // Google user ID
        String email,
        String name,
        String picture
) {
    public static OAuth2UserInfo fromAttributes(Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("picture")
        );
    }
}
