package com.jewelry.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtService {
    String generateAccessToken(Long userId, String email, List<String> roles);
    String generateToken(Map<String, Object> extraClaims, String subject, long expirationMs);
    String extractUsername(String token);
    Long extractUserId(String token);
    List<String> extractRoles(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
