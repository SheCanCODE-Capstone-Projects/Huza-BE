package com.huza.huzabackend.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.huza.huzabackend.service.UserService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {
    private final UserService userService;

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    public OtpService(UserService userService) {
        this.userService = userService;
    }

    public String generateOtp(String email) {
        // Generate 6-digit OTP
        int otp = 100000 + secureRandom.nextInt(900000);
        String otpString = String.valueOf(otp);

        // Store with expiry
        OtpData otpData = new OtpData(otpString, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpStorage.put(email, otpData);

        log.info("🔐 OTP generated for {}: {} (expires in {} minutes)", email, otpString, OTP_EXPIRY_MINUTES);
        return otpString;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            log.warn("⚠️ No OTP found for email: {}", email);
            return false;
        }

        if (otpData.isExpired()) {
            log.warn("⚠️ OTP expired for email: {}", email);
            otpStorage.remove(email);
            return false;
        }

        if (!otpData.getOtp().equals(otp)) {
            log.warn("⚠️ Invalid OTP for email: {}", email);
            return false;
        }

        log.info("✅ OTP verified successfully for email: {}", email);
        otpStorage.remove(email); // Remove after successful verification
        return true;
    }

    public void invalidateOtp(String email) {
        otpStorage.remove(email);
        log.info("🗑️ OTP invalidated for email: {}", email);
    }

    /**
     * Generate OTP for password reset
     */
    public String generateAndSendOtpForPasswordReset(String email) {
        // 1. Check if user exists
        if (!userService.existsByEmail(email)) {
            log.warn("⚠️ Password reset attempted for non-existent email: {}", email);
            throw new RuntimeException("User not found with email: " + email);
        }

        // 2. Generate OTP
        String otp = generateOtp(email);
        log.info("🔑 Password reset OTP generated for {}: {}", email, otp);

        // 3. Return the OTP so controller can send it
        return otp;
    }
    /**
     * Verify OTP for password reset
     * This only verifies the OTP without any side effects
     */
    public boolean verifyOtpForPasswordReset(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            log.warn("⚠️ No OTP found for email: {}", email);
            return false;
        }

        if (otpData.isExpired()) {
            log.warn("⚠️ OTP expired for email: {}", email);
            otpStorage.remove(email);
            return false;
        }

        if (!otpData.getOtp().equals(otp)) {
            log.warn("⚠️ Invalid OTP for email: {}", email);
            return false;
        }

        log.info("✅ OTP verified successfully for password reset: {}", email);
        // Note: We do NOT remove the OTP here!
        // It will be removed after successful password reset
        return true;
    }
// ========================================
// FORGOT PASSWORD / RESET PASSWORD METHODS
// ========================================

    /**
     * Validate OTP for password reset (without removing it)
     */
    public void validateOtpForReset(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            log.warn("⚠️ No OTP found for email: {}", email);
            throw new RuntimeException("Invalid or expired OTP. Please request a new one.");
        }

        if (otpData.isExpired()) {
            log.warn("⚠️ OTP expired for email: {}", email);
            otpStorage.remove(email);
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        if (!otpData.getOtp().equals(otp)) {
            log.warn("⚠️ Invalid OTP for email: {}", email);
            throw new RuntimeException("Invalid OTP. Please check and try again.");
        }

        log.info("✅ OTP validated for password reset: {}", email);
        // Note: OTP is NOT removed here to prevent reuse
    }

    /**
     * Invalidate OTP after successful password reset
     */
    public void invalidateOtpAfterReset(String email) {
        otpStorage.remove(email);
        log.info("🗑️ OTP invalidated after password reset for: {}", email);
    }

    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }
}