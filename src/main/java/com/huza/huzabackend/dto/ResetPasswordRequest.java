package com.huza.huzabackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body for resetting password")
public class ResetPasswordRequest {

    @Schema(description = "User email address (optional - for backend)",
            example = "namahsando@gmail.com",
            required = false)
    private String email;

    @Schema(description = "6-digit OTP code (optional - for backend)",
            example = "123456",
            required = false)
    private String otp;

    @Schema(description = "New password (min 8 chars, must contain uppercase, lowercase and number)",
            example = "NewPassword123",
            required = true)
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Password must contain at least one number, one uppercase and one lowercase letter"
    )
    private String newPassword;

    @Schema(description = "Confirm password (must match newPassword)",
            example = "NewPassword123",
            required = true)
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}