package com.jewelry.auth.service;

import com.jewelry.auth.dto.request.LoginRequest;
import com.jewelry.auth.dto.request.RefreshTokenRequest;
import com.jewelry.auth.dto.request.RegisterRequest;
import com.jewelry.auth.dto.response.AuthResponse;
import com.jewelry.auth.dto.response.TokenResponse;
import com.jewelry.auth.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenResponse refreshToken(RefreshTokenRequest request);
    void logout(String email);
}
