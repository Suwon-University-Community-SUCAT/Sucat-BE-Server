package com.Sucat.domain.token.repository;

import com.Sucat.domain.token.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT u FROM Token u WHERE u.email = :email")
    Optional<Token> findByEmail(String email);

    @Query("SELECT u FROM Token u WHERE u.token = :token")
    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token u WHERE u.email = :email")
    void deleteByEmail(String email);
}
