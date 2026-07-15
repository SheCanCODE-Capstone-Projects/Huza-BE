package com.example.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    // ✅ Role selection: ARTIST, RECRUITER, ADMIN
    @NotBlank(message = "Role is required")
    private String role;  // "ARTIST", "RECRUITER", or "ADMIN"

    // ✅ Recruiter type (only for RECRUITER role)
    private String recruiterType;  // "INDIVIDUAL" or "COMPANY"

    // ✅ Full name (combined first and last name)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    // ✅ Phone number (Rwandan format: +250 7XX XXX XXX)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+250[0-9]{9}$", message = "Phone number must be in format +250XXXXXXXXX")
    private String phoneNumber;

    // ✅ Location
    private String location;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    )
    private String password;


    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;


    @AssertTrue(message = "You must agree to the Terms of Service")
    private boolean agreeToTerms;


    private String tinNumber;


    private String nationalId;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}