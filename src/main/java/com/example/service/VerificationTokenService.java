package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.exception.InvalidTokenException;
import com.example.auth.exception.TokenExpiredException;
import com.example.auth.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserService userService;  // Your service

    @Value("${app.verification.token.expiration}")
    private long tokenExpirationMs;

    // YOUR TASK: Generate verification token
    @Transactional
    public VerificationToken createVerificationToken(User user) {
        log.info("Creating verification token for user: {}", user.getId());

        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        String token = generateToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(tokenExpirationMs / 1000);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .isUsed(false)
                .build();

        return tokenRepository.save(verificationToken);
    }

    // YOUR TASK: Verify account with token
    @Transactional
    public User verifyAccount(String token) {
        log.info("Verifying account with token: {}", token);

        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException("Verification token has expired");
        }

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        // Verify user (calls your UserService)
        User user = verificationToken.getUser();
        return userService.verifyUser(user.getId());
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void deleteToken(String token) {
        tokenRepository.findByToken(token)
                .ifPresent(tokenRepository::delete);
    }
}