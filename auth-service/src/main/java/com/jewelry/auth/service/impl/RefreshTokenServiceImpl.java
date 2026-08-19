package com.jewelry.auth.service.impl;

import com.jewelry.auth.config.JwtConfig;
import com.jewelry.auth.entity.RefreshToken;
import com.jewelry.auth.entity.User;
import com.jewelry.auth.exception.TokenExpiredException;
import com.jewelry.auth.repository.RefreshTokenRepository;
import com.jewelry.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiration() / 1000))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now()) || token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token was expired or revoked. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserRefreshTokens(user);
    }
}
