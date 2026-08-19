package com.jewelry.auth.controller;

import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.enums.AccountStatus;
import com.jewelry.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Endpoints for administration and user management")
public class AdminController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users", description = "Paginated list of registered users (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns details of a specific user (Admin only)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user account status", description = "Change user status to ACTIVE, INACTIVE, LOCKED, or SUSPENDED (Admin only)")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        UserResponse response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
