package huza.huzabackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Sends a verification email with OTP code
     */
    public void sendVerificationEmail(String to, String username, String otp) {
        try {
            String subject = "Verify Your Account - Huza Auth Service";
            String content = buildVerificationEmailContent(username, otp);

            sendEmail(to, subject, content);
            log.info("📧 Verification email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error(" Failed to send verification email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a password reset email with OTP
     */
    public void sendPasswordResetEmail(String to, String username, String otp) {
        try {
            String subject = "Password Reset Request - Huza Auth Service";
            String content = buildPasswordResetEmailContent(username, otp);

            sendEmail(to, subject, content);
            log.info(" Password reset email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error(" Failed to send password reset email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
        }
    }

    private void sendEmail(String to, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, "Huza Auth Service");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
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
}