package com.huza.huzabackend.config;

import com.huza.huzabackend.controller.AuthController;
import com.huza.huzabackend.dto.RegisterRequest;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.UserRepository;
import com.huza.huzabackend.service.EmailService;
import com.huza.huzabackend.service.OtpService;
import com.huza.huzabackend.service.UserService;
import com.huza.huzabackend.service.VerificationTokenService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, SwaggerConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenAPI openAPI;

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
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void swaggerShouldExposeGoogleOAuth2SecurityScheme() {
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("google_oauth2");

        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.OAUTH2);
    }

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
