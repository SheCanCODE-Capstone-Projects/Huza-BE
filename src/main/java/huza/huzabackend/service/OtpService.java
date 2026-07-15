package huza.huzabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

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