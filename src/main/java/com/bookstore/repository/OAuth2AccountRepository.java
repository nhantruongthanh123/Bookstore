package com.bookstore.repository;

import com.bookstore.entity.OAuth2Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AccountRepository extends JpaRepository<OAuth2Account,Long> {
    Optional<OAuth2Account> findByProviderAndProviderUserId(String provider, String providerUserId);
}
