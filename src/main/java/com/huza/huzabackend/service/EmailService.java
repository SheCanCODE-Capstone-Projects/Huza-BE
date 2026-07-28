package com.huza.huzabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

//    private final JavaMailSender mailSender;
@Value("${BREVO_API_KEY}")
private String brevoApiKey;

    @Value("${MAIL_FROM}")
    private String fromEmail;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Sends a verification email with OTP code (runs in background — does not block the caller)
     */
    @Async
    public void sendVerificationEmail(String to, String username, String otp) {
        try {
            String subject = "Verify Your Account - Huza Auth Service";
            String content = buildVerificationEmailContent(username, otp);

            sendEmail(to, subject, content);
            log.info("📧 Verification email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error(" Failed to send verification email to {}: {}", to, e.getMessage());
            // Don't rethrow here — this runs async, an uncaught exception just gets logged.
            // Rethrowing would only be visible in server logs, not to the original caller.
        }
    }

    /**
     * Sends a password reset email with OTP (runs in background)
     */
    @Async
    public void sendPasswordResetEmail(String to, String username, String otp) {
        try {
            String subject = "Password Reset Request - Huza Auth Service";
            String content = buildPasswordResetEmailContent(username, otp);

            sendEmail(to, subject, content);
            log.info(" Password reset email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error(" Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Send OTP for password reset (runs in background)
     */
    @Async
    public void sendPasswordResetOtp(String to, String otp) {
        try {
            String subject = "🔐 Password Reset OTP - Huza";
            String content = buildPasswordResetOtpContent(otp);
            sendEmail(to, subject, content);
            log.info("📧 Password reset OTP sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String content) {

        OkHttpClient client = new OkHttpClient();

        String json = """
    {
      "sender": {
        "name": "Huza Auth Service",
        "email": "%s"
      },
      "to": [
        {
          "email": "%s"
        }
      ],
      "subject": "%s",
      "htmlContent": %s
    }
    """.formatted(
                fromEmail,
                to,
                subject,
                "\"" + content
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        + "\""
        );


        Request request = new Request.Builder()
                .url("https://api.brevo.com/v3/smtp/email")
                .addHeader("api-key", brevoApiKey)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        json,
                        MediaType.parse("application/json")
                ))
                .build();


        try (Response response = client.newCall(request).execute()) {

            String responseBody = response.body() != null
                    ? response.body().string()
                    : "";

            log.info("Brevo response: {}", responseBody);

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Brevo error: " + response.code() + " " + responseBody
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Brevo connection failed", e);
        }
    }

    private String buildVerificationEmailContent(String username, String otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { padding: 30px; background-color: #f9f9f9; border: 1px solid #ddd; }
                    .otp-code { 
                        font-size: 36px; 
                        font-weight: bold; 
                        text-align: center; 
                        padding: 20px; 
                        background-color: #e8f5e9; 
                        border-radius: 8px; 
                        letter-spacing: 8px;
                        color: #2e7d32;
                        margin: 20px 0;
                    }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #4CAF50; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 4px; 
                    }
                    .expiry { color: #f44336; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Huza! 🎉</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        <p>Thank you for registering with Huza. Please use the One-Time Password (OTP) below to verify your email address:</p>
                        
                        <div class="otp-code">%s</div>
                        
                        <p>This OTP is valid for <span class="expiry">10 minutes</span>.</p>
                        <p>If you didn't create an account with us, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message, please do not reply to this email.</p>
                        <p>&copy; 2024 Huza Auth Service. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, username, otp);
    }

    private String buildPasswordResetEmailContent(String username, String otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #ff6b6b; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { padding: 30px; background-color: #f9f9f9; border: 1px solid #ddd; }
                    .otp-code { 
                        font-size: 36px; 
                        font-weight: bold; 
                        text-align: center; 
                        padding: 20px; 
                        background-color: #fff3e0; 
                        border-radius: 8px; 
                        letter-spacing: 8px;
                        color: #e65100;
                        margin: 20px 0;
                    }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .expiry { color: #f44336; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request </h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        <p>We received a request to reset your password. Use the OTP below:</p>
                        
                        <div class="otp-code">%s</div>
                        
                        <p>This OTP is valid for <span class="expiry">10 minutes</span>.</p>
                        <p>If you didn't request this, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message, please do not reply to this email.</p>
                        <p>&copy; 2024 Huza Auth Service. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, username, otp);

    }

    /**
     * Build password reset OTP email content
     */
    private String buildPasswordResetOtpContent(String otp) {
        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #ff6b6b; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                .content { padding: 30px; background-color: #f9f9f9; border: 1px solid #ddd; }
                .otp-code { 
                    font-size: 36px; 
                    font-weight: bold; 
                    text-align: center; 
                    padding: 20px; 
                    background-color: #fff3e0; 
                    border-radius: 8px; 
                    letter-spacing: 8px;
                    color: #e65100;
                    margin: 20px 0;
                }
                .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                .expiry { color: #f44336; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Password Reset Request 🔐</h1>
                </div>
                <div class="content">
                    <p>Hello,</p>
                    <p>We received a request to reset your password. Use the OTP below:</p>
                    
                    <div class="otp-code">%s</div>
                    
                    <p>This OTP is valid for <span class="expiry">10 minutes</span>.</p>
                    <p>If you didn't request this, please ignore this email.</p>
                </div>
                <div class="footer">
                    <p>This is an automated message, please do not reply to this email.</p>
                    <p>&copy; 2024 Huza Auth Service. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """, otp);
    }
}