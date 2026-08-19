package com.jewelry.auth.service;

import com.jewelry.auth.dto.request.ChangePasswordRequest;
import com.jewelry.auth.dto.request.UpdateProfileRequest;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getCurrentUserProfile(String email);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUserStatus(Long id, AccountStatus status);
}
