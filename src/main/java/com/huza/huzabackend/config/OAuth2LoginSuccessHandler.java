package com.huza.huzabackend.config;

import com.huza.huzabackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Object userId = oAuth2User.getAttribute("huzaUserId");
        String role = oAuth2User.getAttribute("huzaRole");
        String fullName = oAuth2User.getAttribute("huzaFullName");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("")
                .authorities(role != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        : List.of())
                .build();

        Map<String, Object> extraClaims = new HashMap<>();
        if (userId != null) extraClaims.put("userId", userId);
        if (role != null) extraClaims.put("role", role);
        if (fullName != null) extraClaims.put("fullName", fullName);

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        // Redirect directly back to Swagger UI with the token in query parameters
        String redirectUrl = UriComponentsBuilder.fromPath("/swagger-ui/index.html")
                .queryParam("token", jwtToken)
                .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}