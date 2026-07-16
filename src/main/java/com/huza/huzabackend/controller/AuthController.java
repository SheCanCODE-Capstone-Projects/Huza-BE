package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ForgotPasswordRequest;
import com.huza.huzabackend.dto.RegisterRequest;
import com.huza.huzabackend.dto.OtpRequest;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.service.EmailService;
import com.huza.huzabackend.service.OtpService;
import com.huza.huzabackend.service.UserService;
import com.huza.huzabackend.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("📝 Registration request for email: {}", request.getEmail());

        try {
            // Register user
            User user = userService.registerUser(request);

            // Generate OTP
            String otp = otpService.generateOtp(user.getEmail());

            // Send OTP via email
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), otp);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Registration successful! Please check your email for OTP.",
                            "User registered with ID: " + user.getId()
                    ));

        } catch (Exception e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    //Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        log.info("🔑 Password reset requested for email: {}", request.getEmail());

        try {
            // 1. Generate OTP and get the value
            String otp = otpService.generateAndSendOtpForPasswordReset(request.getEmail());

            // 2. Send OTP via email
            emailService.sendPasswordResetOtp(request.getEmail(), otp);

            return ResponseEntity.ok(ApiResponse.success(
                    "OTP sent to your email. Please check your inbox.",
                    "OTP sent to: " + request.getEmail()
            ));

        } catch (Exception e) {
            log.error("❌ Forgot password failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody OtpRequest request) {
        log.info(" Verifying OTP for email: {}", request.getEmail());

        try {
            // Verify OTP
            boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid or expired OTP. Please try again."));
            }

            // Get user and mark as verified
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Mark user as verified
            userService.verifyUser(user.getId());

            return ResponseEntity.ok(ApiResponse.success(
                    "Account verified successfully! You can now login.",
                    "User verified: " + user.getUsername()
            ));

        } catch (Exception e) {
            log.error(" OTP verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(@RequestParam String email) {
        log.info("📧 Resending OTP to: {}", email);

        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            if (user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("User is already verified"));
            }

            // Generate new OTP
            String otp = otpService.generateOtp(user.getEmail());
            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), otp);

            return ResponseEntity.ok(ApiResponse.success(
                    "OTP resent successfully",
                    "Check your email for the new OTP"
            ));

        } catch (Exception e) {
            log.error(" Failed to resend OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to resend OTP: " + e.getMessage()));
        }
    }
}