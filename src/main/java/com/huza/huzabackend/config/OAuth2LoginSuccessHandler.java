package com.huza.huzabackend.config;

import com.huza.huzabackend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String userId = oAuth2User.getAttribute("huzaUserId");
        String role = oAuth2User.getAttribute("huzaRole");
        String fullName = oAuth2User.getAttribute("huzaFullName");

        // JwtService.generateToken needs a UserDetails; the subject (getUsername())
        // becomes the token's "sub" claim. In your system, username == email
        // (see UserService.registerUser: .username(request.getEmail())).
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("") // unused for token generation, never checked here
                .authorities(role != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        : List.of())
                .build();

        Map<String, Object> extraClaims = new HashMap<>();
        putIfPresent(extraClaims, "userId", userId);
        putIfPresent(extraClaims, "role", role);
        putIfPresent(extraClaims, "fullName", fullName);

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("email", email);
        putIfPresent(responseBody, "fullName", fullName);
        putIfPresent(responseBody, "role", role);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }

    private void putIfPresent(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}
