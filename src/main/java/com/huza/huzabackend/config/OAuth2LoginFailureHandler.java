package com.huza.huzabackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorCode = (exception instanceof OAuth2AuthenticationException oae)
                ? oae.getError().getErrorCode()
                : "authentication_failed";

        boolean missingAuthorizationResponse = request.getParameter("code") == null
                || request.getParameter("state") == null;

        int status = missingAuthorizationResponse
                ? HttpServletResponse.SC_BAD_REQUEST
                : "user_not_found".equals(errorCode)
                  ? HttpServletResponse.SC_NOT_FOUND
                  : HttpServletResponse.SC_UNAUTHORIZED;

        String message = missingAuthorizationResponse
                ? "Invalid OAuth2 callback. Start sign-in at /login or open the authorizationUrl returned by /api/auth/oauth2/google."
                : "user_not_found".equals(errorCode)
                  ? "User not found. Please register before logging in with Google."
                  : "Google authentication failed. Verify GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, and authorized redirect URIs.";

        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), Map.of(
                "success", false,
                "message", message
        ));
    }
}