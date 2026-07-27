package com.huza.huzabackend.config;

import com.huza.huzabackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerOAuth2SuccessHandlerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OAuth2LoginSuccessHandler handler;

    @Test
    void shouldRedirectToSwaggerUiWithTokenAfterSuccessfulAuthentication() throws Exception {
        // Arrange
        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Authentication authentication = Mockito.mock(Authentication.class);
        OAuth2User oAuth2User = Mockito.mock(OAuth2User.class);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("user@example.com");
        when(oAuth2User.getAttribute("huzaUserId")).thenReturn(1L);
        when(oAuth2User.getAttribute("huzaRole")).thenReturn("ARTIST");
        when(oAuth2User.getAttribute("huzaFullName")).thenReturn("Test User");

        when(jwtService.generateToken(anyMap(), any())).thenReturn("mocked-jwt-token");

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        String redirectedUrl = response.getRedirectedUrl();
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.startsWith("/swagger-ui/index.html?token=mocked-jwt-token"));
        assertTrue(redirectedUrl.contains("email=user%40example.com"));
    }
}