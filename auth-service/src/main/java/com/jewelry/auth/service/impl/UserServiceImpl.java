package com.jewelry.auth.service.impl;

import com.jewelry.auth.dto.request.ChangePasswordRequest;
import com.jewelry.auth.dto.request.UpdateProfileRequest;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.User;
import com.jewelry.auth.entity.enums.AccountStatus;
import com.jewelry.auth.exception.InvalidCredentialsException;
import com.jewelry.auth.exception.ResourceNotFoundException;
import com.jewelry.auth.mapper.UserMapper;
import com.jewelry.auth.repository.UserRepository;
import com.jewelry.auth.service.RefreshTokenService;
import com.jewelry.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String email) {
        User user = getUserByEmail(email);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmail(email);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens on password change
        refreshTokenService.revokeAllUserTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, AccountStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setAccountStatus(status);
        if (status != AccountStatus.ACTIVE) {
            user.setEnabled(false);
            refreshTokenService.revokeAllUserTokens(user);
        } else {
            user.setEnabled(true);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
