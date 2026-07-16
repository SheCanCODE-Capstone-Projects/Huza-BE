package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.RegisterRequest;
import com.huza.huzabackend.entity.RecruiterType;
import com.huza.huzabackend.entity.Role;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.entity.UserStatus;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== REGISTRATION =====

    @Transactional
    public User registerUser(RegisterRequest request) {
        log.info("📝 Registering new user with email: {}", request.getEmail());

        // Validate password match
        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate unique fields
        validateUniqueFields(request);

        // Map role from request
        Role role = mapRole(request.getRole());

        // Build user
        User.UserBuilder builder = User.builder()
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .location(request.getLocation())
                .role(role)
                .status(UserStatus.PENDING_VERIFICATION)
                .isVerified(false)
                .otpVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .failedLoginAttempts(0);

        // If recruiter, add recruiter type and TIN
        if (role == Role.RECRUITER) {
            RecruiterType recruiterType = mapRecruiterType(request.getRecruiterType());
            builder.recruiterType(recruiterType);

            if (recruiterType == RecruiterType.COMPANY && request.getTinNumber() != null) {
                builder.tinNumber(request.getTinNumber());
            }
            if (recruiterType == RecruiterType.INDIVIDUAL && request.getNationalId() != null) {
                builder.nationalId(request.getNationalId());
            }
        }

        User savedUser = userRepository.save(builder.build());
        log.info("✅ User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    private Role mapRole(String roleStr) {
        if (roleStr == null) return Role.USER;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }

    private RecruiterType mapRecruiterType(String type) {
        if (type == null) return RecruiterType.INDIVIDUAL;
        try {
            return RecruiterType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RecruiterType.INDIVIDUAL;
        }
    }

    private void validateUniqueFields(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }
    }

    // ===== VERIFICATION =====

    @Transactional
    public User verifyUser(String userId) {
        log.info("🔐 Verifying user with ID: {}", userId);

        User user = findById(userId);
        user.activate();
        user.setStatus(UserStatus.ACTIVE);
        user.setOtpVerified(true);

        User updatedUser = userRepository.save(user);
        log.info("✅ User verified successfully: {}", userId);

        return updatedUser;
    }

    // ===== FIND METHODS =====

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ===== PROFILE MANAGEMENT =====

    @Transactional
    public User updateProfile(String userId, RegisterRequest request) {
        log.info("✏️ Updating profile for user ID: {}", userId);

        User user = findById(userId);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DuplicateResourceException("Phone number already registered");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // ===== STATUS MANAGEMENT =====

    @Transactional
    public User updateUserStatus(String userId, UserStatus status) {
        log.info("📊 Updating status for user ID: {} to {}", userId, status);

        User user = findById(userId);
        user.setStatus(status);

        if (status == UserStatus.ACTIVE) {
            user.setVerified(true);
        }

        return userRepository.save(user);
    }

    /**
     * Reset user password
     */
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("🔑 Password reset successfully for user: {}", email);
    }

    @Transactional
    public User updateUserRole(String userId, Role role) {
        log.info("🎭 Updating role for user ID: {} to {}", userId, role);

        User user = findById(userId);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ===== DELETE =====

    @Transactional
    public void deleteUser(String userId) {
        log.info("🗑️ Deleting user with ID: {}", userId);

        User user = findById(userId);
        userRepository.delete(user);

        log.info("✅ User deleted successfully: {}", userId);
    }

    // ===== LOGIN HELPERS (For KEZA) =====

    @Transactional
    public User updateLastLogin(String userId) {
        User user = findById(userId);
        user.setLastLogin(LocalDateTime.now());
        user.resetFailedLoginAttempts();
        return userRepository.save(user);
    }

    @Transactional
    public void recordFailedLoginAttempt(String userId) {
        User user = findById(userId);
        user.incrementFailedLoginAttempts();
        userRepository.save(user);
    }

    public boolean isUserVerified(String username) {
        return userRepository.findByUsername(username)
                .map(User::isVerified)
                .orElse(false);
    }

    public boolean isUserActive(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getStatus() == UserStatus.ACTIVE)
                .orElse(false);
    }
}