package com.huza.huzabackend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SwaggerOAuth2SuccessHandlerTest {

    @Test
    void shouldRedirectToSwaggerUiAfterSuccessfulAuthentication() throws Exception {
        SwaggerOAuth2SuccessHandler handler = new SwaggerOAuth2SuccessHandler();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = Mockito.mock(Authentication.class);

        handler.onAuthenticationSuccess(request, response, authentication);

        String redirectedUrl = ((MockHttpServletResponse) response).getRedirectedUrl();
        assertEquals("/swagger-ui/index.html", redirectedUrl);
    }
}
