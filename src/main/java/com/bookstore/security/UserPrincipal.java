package com.bookstore.security;

import com.bookstore.entity.User;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NullMarked
public class UserPrincipal implements OAuth2User, UserDetails {
    @Nullable
    private final User user;
    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes = Collections.emptyMap();

    public UserPrincipal(User user) {
        this.user = user;
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getEnabled();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    public UserPrincipal(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.user = null;
        this.id = id;
        this.username = username;
        this.password = "";
        this.enabled = true;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        userPrincipal.attributes = attributes;
        return userPrincipal;
    }

    @Override
    public String getName() { return String.valueOf(id); }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override public boolean isEnabled() { return enabled; }
}
