package com.Sucat.domain.token.repository;

import com.Sucat.domain.token.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("SELECT u FROM RefreshToken u WHERE u.email = :email")
    RefreshToken findByEmail(String email);

    @Query("SELECT u FROM RefreshToken u WHERE u.token = :token")
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken u WHERE u.email = :email")
    void deleteByEmail(String email);
}
