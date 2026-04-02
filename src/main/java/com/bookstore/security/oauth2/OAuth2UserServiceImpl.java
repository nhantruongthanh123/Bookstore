package com.bookstore.security.oauth2;

import com.bookstore.dto.Auth.OAuth2UserInfo;
import com.bookstore.entity.OAuth2Account;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.OAuth2AccountRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {
    private final UserRepository userRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User processOAuth2User(String provider, OAuth2UserInfo userInfo) {
        return oauth2AccountRepository.findByProviderAndProviderUserId(provider, userInfo.id())
                .map(account -> userRepository.findById(account.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("Can't find user.")))
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(userInfo.email())
                            .orElseGet(() -> registerNewUser(userInfo));

                    linkOAuth2Account(user, provider, userInfo);
                    return user;
                });
    }

    private User registerNewUser(OAuth2UserInfo userInfo) {
        User user = new User();
        user.setEmail(userInfo.email());
        user.setFullName(userInfo.name());
        user.setUsername(userInfo.email());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default Role not found."));
        user.setRoles(Set.of(userRole));
        return userRepository.save(user);
    }

    private void linkOAuth2Account(User user, String provider, OAuth2UserInfo userInfo) {
        OAuth2Account account = OAuth2Account.builder()
                .user(user)
                .provider(provider)
                .providerUserId(userInfo.id())
                .email(userInfo.email())
                .build();
        oauth2AccountRepository.save(account);
    }

}
