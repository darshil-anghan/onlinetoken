package com.example.mytoken.repository;

import com.example.mytoken.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmail(String email);
    Optional<OtpToken> findByUserId(Long userId);
}
