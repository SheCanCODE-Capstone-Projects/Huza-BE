package com.huza.huzabackend.controller;

import java.net.InetSocketAddress;
import java.net.Socket;
import com.huza.huzabackend.dto.*;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.service.EmailService;
import com.huza.huzabackend.service.OtpService;
import com.huza.huzabackend.service.UserService;
import com.huza.huzabackend.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @GetMapping("/test-mail-connection")
    public String testMailConnection() {
        try {
            Socket socket = new Socket();
            socket.connect(
                    new InetSocketAddress("smtp-relay.brevo.com", 587),
                    5000
            );
            socket.close();

            return "SMTP connection successful";

        } catch (Exception e) {
            return "SMTP failed: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔑 Login request for email: {}", request.getEmail());
        // Exceptions (InvalidCredentialsException, AccountNotVerifiedException,
        // AccountBannedException) bubble up and are handled by GlobalExceptionHandler
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

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

    @PostMapping("/verify-otp-reset")
    @Operation(
            summary = "Verify OTP for password reset",
            description = "Verifies the OTP for password reset (does NOT mark user as verified)"
    )
    public ResponseEntity<ApiResponse<String>> verifyOtpForReset(
            @Valid @RequestBody VerifyOtpForResetRequest request) {

        log.info("🔐 Verifying OTP for password reset: {}", request.getEmail());

        try {
            // Verify OTP (without marking user as verified)
            boolean isValid = otpService.verifyOtpForPasswordReset(
                    request.getEmail(),
                    request.getOtp()
            );

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid or expired OTP. Please try again."));
            }

            return ResponseEntity.ok(ApiResponse.success(
                    "OTP verified successfully. You can now reset your password.",
                    "OTP verified for: " + request.getEmail()
            ));

        } catch (Exception e) {
            log.error("❌ OTP verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Verification failed: " + e.getMessage()));
        }
    }


    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Resets the user's password with new password and confirm password"
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        log.info("🔑 Password reset requested");

        try {
            // 1. Check if passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                log.warn("⚠️ Passwords do not match");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Passwords do not match. Please make sure both passwords are identical."));
            }

            // 2. Validate OTP
            otpService.validateOtpForReset(request.getEmail(), request.getOtp());

            // 3. Reset password
            userService.resetPassword(request.getEmail(), request.getNewPassword());

            // 4. Invalidate OTP after successful reset
            otpService.invalidateOtpAfterReset(request.getEmail());

            log.info("✅ Password reset successfully for email: {}", request.getEmail());

            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset successfully! You can now login with your new password.",
                    "Password reset for: " + request.getEmail()
            ));

        } catch (RuntimeException e) {
            log.error("❌ Password reset failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Password reset failed: " + e.getMessage()));
        }
    }
}