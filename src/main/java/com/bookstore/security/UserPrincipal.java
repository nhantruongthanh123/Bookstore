package com.bookstore.security;

import com.bookstore.entity.User;
import lombok.Getter;
import java.util.Collection;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NullMarked
public class UserPrincipal implements OAuth2User, UserDetails {
    private final User user;
    private Map<String, Object> attributes = Collections.emptyMap();

    public UserPrincipal(User user) {
        this.user = user;
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        userPrincipal.attributes = attributes;
        return userPrincipal;
    }

    @Override
    public String getName() { return String.valueOf(user.getId()); }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    @Override public boolean isEnabled() { return user.getEnabled(); }
}
