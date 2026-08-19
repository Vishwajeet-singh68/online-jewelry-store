package com.jewelry.auth.service;

import com.jewelry.auth.entity.RefreshToken;
import com.jewelry.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void revokeToken(RefreshToken token);
    void revokeAllUserTokens(User user);
}
