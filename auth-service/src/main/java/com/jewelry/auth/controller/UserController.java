package com.jewelry.auth.controller;

import com.jewelry.auth.dto.request.ChangePasswordRequest;
import com.jewelry.auth.dto.request.UpdateProfileRequest;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for authenticated user profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get profile", description = "Returns profile of currently authenticated user")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse response = userService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update profile", description = "Updates allowed profile fields for authenticated user")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change password", description = "Updates password and revokes active refresh tokens")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
