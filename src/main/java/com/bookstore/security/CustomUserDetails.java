package com.bookstore.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Long id;
    private final String email;
    private final String fullName;

    public CustomUserDetails(Long id,
                             String username,
                             String password,
                             String email,
                             String fullName,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }
}
