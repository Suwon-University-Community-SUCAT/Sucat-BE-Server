package com.Sucat.domain.token.repository;

import com.Sucat.domain.token.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT u FROM Token u WHERE u.email = :email")
    Optional<Token> findByEmail(String email);

    Optional<Token> findByAccessToken(String accessToken);

    boolean existsByAccessToken(String accessToken);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token u WHERE u.email = :email")
    void deleteByEmail(String email);

    void deleteByCreatedAt(@Param("date") final LocalDateTime date);
}
