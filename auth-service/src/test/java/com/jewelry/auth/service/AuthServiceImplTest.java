package com.jewelry.auth.service;

import com.jewelry.auth.config.JwtConfig;
import com.jewelry.auth.dto.request.LoginRequest;
import com.jewelry.auth.dto.request.RegisterRequest;
import com.jewelry.auth.dto.response.AuthResponse;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.RefreshToken;
import com.jewelry.auth.entity.Role;
import com.jewelry.auth.entity.User;
import com.jewelry.auth.entity.enums.AccountStatus;
import com.jewelry.auth.entity.enums.RoleName;
import com.jewelry.auth.exception.UserAlreadyExistsException;
import com.jewelry.auth.mapper.UserMapper;
import com.jewelry.auth.repository.RoleRepository;
import com.jewelry.auth.repository.UserRepository;
import com.jewelry.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("Password@123")
                .phoneNumber("9876543210")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encoded_password")
                .accountStatus(AccountStatus.ACTIVE)
                .enabled(true)
                .roles(Set.of(Role.builder().name(RoleName.ROLE_CUSTOMER).build()))
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(roleRepository.findByName(any())).thenReturn(Optional.of(Role.builder().name(RoleName.ROLE_CUSTOMER).build()));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().id(1L).email("john@example.com").build());

        UserResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "Password@123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access_token");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(RefreshToken.builder().token("refresh_token").build());
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(900000L);
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().id(1L).email("john@example.com").build());

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
    }
}
