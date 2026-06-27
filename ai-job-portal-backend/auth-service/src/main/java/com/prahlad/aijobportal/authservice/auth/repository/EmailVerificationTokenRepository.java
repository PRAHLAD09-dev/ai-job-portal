package com.prahlad.aijobportal.authservice.auth.repository;

import com.prahlad.aijobportal.authservice.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
}
