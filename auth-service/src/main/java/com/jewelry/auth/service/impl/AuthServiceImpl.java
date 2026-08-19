package com.jewelry.auth.service.impl;

import com.jewelry.auth.config.JwtConfig;
import com.jewelry.auth.dto.request.LoginRequest;
import com.jewelry.auth.dto.request.RefreshTokenRequest;
import com.jewelry.auth.dto.request.RegisterRequest;
import com.jewelry.auth.dto.response.AuthResponse;
import com.jewelry.auth.dto.response.TokenResponse;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.RefreshToken;
import com.jewelry.auth.entity.Role;
import com.jewelry.auth.entity.User;
import com.jewelry.auth.entity.enums.AccountStatus;
import com.jewelry.auth.entity.enums.RoleName;
import com.jewelry.auth.exception.InvalidCredentialsException;
import com.jewelry.auth.exception.InvalidTokenException;
import com.jewelry.auth.exception.ResourceNotFoundException;
import com.jewelry.auth.exception.UserAlreadyExistsException;
import com.jewelry.auth.mapper.UserMapper;
import com.jewelry.auth.repository.RoleRepository;
import com.jewelry.auth.repository.UserRepository;
import com.jewelry.auth.service.AuthService;
import com.jewelry.auth.service.JwtService;
import com.jewelry.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEnabled(true);

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_CUSTOMER).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE || !user.isEnabled()) {
            throw new InvalidCredentialsException("Account is disabled or locked");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    if (user.getAccountStatus() != AccountStatus.ACTIVE || !user.isEnabled()) {
                        throw new InvalidCredentialsException("Account is disabled or locked");
                    }
                    List<String> roles = user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toList());

                    String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);
                    RefreshToken rotatedRefreshToken = refreshTokenService.createRefreshToken(user);

                    return TokenResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(rotatedRefreshToken.getToken())
                            .tokenType("Bearer")
                            .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                            .build();
                })
                .orElseThrow(() -> new InvalidTokenException("Refresh token is not present in database"));
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        refreshTokenService.revokeAllUserTokens(user);
    }
}
