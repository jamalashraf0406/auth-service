package com.bluesoft.authservice.services;

import com.bluesoft.authservice.entities.RefreshToken;
import com.bluesoft.authservice.repositories.RefreshTokenRepository;
import com.bluesoft.authservice.utils.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken create(String username) {
        String createToken = RefreshTokenUtil.generate();
        RefreshToken token = RefreshToken.builder()
                .token(RefreshTokenUtil.hash(createToken))
                .username(username)
                .expiryAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();
        token = repository.save(token);
        token.setToken(createToken);
        return token;
    }

    public RefreshToken validate(String token) {
        RefreshToken refreshToken = repository.findByToken(RefreshTokenUtil.hash(token))
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() ||
                refreshToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }
        return refreshToken;
    }

    public void revoke(String token) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }
}

