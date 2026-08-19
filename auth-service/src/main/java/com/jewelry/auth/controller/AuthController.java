package com.jewelry.auth.controller;

import com.jewelry.auth.dto.request.LoginRequest;
import com.jewelry.auth.dto.request.RefreshTokenRequest;
import com.jewelry.auth.dto.request.RegisterRequest;
import com.jewelry.auth.dto.response.AuthResponse;
import com.jewelry.auth.dto.response.TokenResponse;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new customer account")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns access & refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Revokes all refresh tokens for the authenticated user")
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            authService.logout(authentication.getName());
        }
        return ResponseEntity.noContent().build();
    }
}
