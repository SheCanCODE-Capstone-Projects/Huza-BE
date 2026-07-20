package com.huza.huzabackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF, Form Login, HTTP Basic
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 2. Apply CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Stateless session management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/auth/**",
                                "/api/auth/**",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/verify-otp",
                                "/api/auth/resend-otp",
                                "/api/auth/reset-password",
                                "/api/auth/verify-otp-reset",
                                "/api/auth/check-email"
                        ).permitAll()
                        // OAuth2 endpoints
                        .requestMatchers("/login/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        // Swagger endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // Artist endpoints (public)
                        .requestMatchers(
                                "/api/artist/profile/**",
                                "/api/artist/skills/**",
                                "/api/skills/**"
                        ).permitAll()
                        // API endpoints (public for testing)
                        .requestMatchers(
                                "/api/**"
                        ).permitAll()
                        // User management endpoints
                        .requestMatchers("/api/users/**").permitAll()
                        // Recruiter endpoints
                        .requestMatchers("/api/recruiter/**").permitAll()
                        // Admin endpoints - requires ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // 5. OAuth2 Login configuration
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/code/*"))
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/swagger-ui.html")
                )

                // 6. Add custom authentication provider
                .authenticationProvider(authenticationProvider)

                // 7. Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow multiple origins for development
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200",
                "http://localhost:8005"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}