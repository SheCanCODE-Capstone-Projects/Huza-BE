package com.huza.huzabackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwaggerOAuth2SuccessHandlerTest {

    @Test
    void shouldRedirectToSwaggerUiAfterSuccessfulAuthentication() throws Exception {
        SwaggerOAuth2SuccessHandler handler = new SwaggerOAuth2SuccessHandler();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = Mockito.mock(Authentication.class);

        handler.onAuthenticationSuccess(request, response, authentication);

        assertEquals("/swagger-ui/index.html", response.getRedirectedUrl());
    }
}
