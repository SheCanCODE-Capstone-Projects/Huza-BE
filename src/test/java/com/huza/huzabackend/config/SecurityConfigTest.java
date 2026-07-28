package com.huza.huzabackend.config;

import com.huza.huzabackend.controller.AuthController;
import com.huza.huzabackend.dto.RegisterRequest;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.UserRepository;
import com.huza.huzabackend.service.EmailService;
import com.huza.huzabackend.service.OtpService;
import com.huza.huzabackend.service.ApplicationService;
import com.huza.huzabackend.service.UserService;
import com.huza.huzabackend.service.VerificationTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private OtpService otpService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @MockBean
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    void registerEndpointShouldBeAccessibleWithoutAuthentication() throws Exception {
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenReturn(User.builder()
                        .id("user-1")
                        .email("jacaci2075@gicont.com")
                        .fullName("Jac aci")
                        .build());
        when(otpService.generateOtp(anyString())).thenReturn("123456");
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        String payload = """
                {
                  "role": "ARTIST",
                  "recruiterType": "INDIVIDUAL",
                  "fullName": "Jac aci",
                  "phoneNumber": "+250781234567",
                  "location": "Kigali, Rwanda",
                  "email": "jacaci2075@gicont.com",
                  "password": "Abcdef1!",
                  "confirmPassword": "Abcdef1!",
                  "agreeToTerms": true,
                  "tinNumber": "119988776",
                  "nationalId": "1199580012345678"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().is2xxSuccessful());
    }
}