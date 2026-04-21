package com.bookstore.repository;

import com.bookstore.entity.RefreshToken;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByToken(String token);

    @Modifying
    void deleteByUser(User user);
    void deleteByExpiryDateBefore(Instant date);
}
