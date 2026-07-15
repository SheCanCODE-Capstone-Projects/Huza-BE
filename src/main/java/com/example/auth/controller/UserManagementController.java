package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.entity.UserStatus;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserService userService;

    // YOUR TASK: Get user profile
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.username")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable String userId) {
        log.info("Fetching user profile for ID: {}", userId);

        try {
            User user = userService.findById(userId);
            return ResponseEntity.ok(ApiResponse.success("User found", user));
        } catch (Exception e) {
            log.error("Failed to fetch user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found: " + e.getMessage()));
        }
    }

    // YOUR TASK: Update profile
    @PutMapping("/{userId}/profile")
    @PreAuthorize("#userId == authentication.principal.username")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody RegisterRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        try {
            User updatedUser = userService.updateProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
        } catch (Exception e) {
            log.error("Failed to update profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Profile update failed: " + e.getMessage()));
        }
    }

    // YOUR TASK: Update user status (active, inactive, suspended)
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam UserStatus status) {
        log.info("Updating status for user ID: {} to {}", userId, status);

        try {
            User updatedUser = userService.updateUserStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.success("User status updated", updatedUser));
        } catch (Exception e) {
            log.error("Failed to update status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }

    // YOUR TASK: Update user role
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable String userId,
            @RequestParam Role role) {
        log.info("Updating role for user ID: {} to {}", userId, role);

        try {
            User updatedUser = userService.updateUserRole(userId, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated", updatedUser));
        } catch (Exception e) {
            log.error("Failed to update role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Role update failed: " + e.getMessage()));
        }
    }

    // YOUR TASK: Delete user
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.username")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        log.info("Deleting user with ID: {}", userId);

        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("User deletion failed: " + e.getMessage()));
        }
    }

    // YOUR TASK: Activate user
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> activateUser(@PathVariable String userId) {
        log.info("Activating user with ID: {}", userId);

        try {
            User user = userService.updateUserStatus(userId, UserStatus.ACTIVE);
            return ResponseEntity.ok(ApiResponse.success("User activated successfully", user));
        } catch (Exception e) {
            log.error("Failed to activate user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Activation failed: " + e.getMessage()));
        }
    }

    // YOUR TASK: Suspend user
    @PostMapping("/{userId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> suspendUser(@PathVariable String userId) {
        log.info("Suspending user with ID: {}", userId);

        try {
            User user = userService.updateUserStatus(userId, UserStatus.SUSPENDED);
            return ResponseEntity.ok(ApiResponse.success("User suspended successfully", user));
        } catch (Exception e) {
            log.error("Failed to suspend user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Suspension failed: " + e.getMessage()));
        }
    }
}