package com.huza.huzabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    // Constructor injection for your authentication components
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler authenticationSuccessHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Explicitly enable CORS configuration handling
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Team settings: Disable standard session/login features
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 3. Session state management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4. Merged authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**", "/login", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
//                        .requestMatchers("/api/moderation/**").hasAnyRole("ADMIN", "MODERATOR")

                        // ADD THIS LINE HERE to let your new endpoints bypass security for public viewing/testing:
                        .requestMatchers("/api/artist/profile/**").permitAll()
                        .requestMatchers("/api/artist/skills/**").permitAll()
                        .requestMatchers("/api/skills/**").permitAll()
//                        .requestMatchers("/api/moderation/**").hasAnyRole("ADMIN", "MODERATOR")   // rule A
                        .requestMatchers("/api/**", "/api/admin/**", "/api/moderation/**").permitAll()  // rule B, contains /api/moderation/** again
//                        .requestMatchers("/api/moderation/**").hasAnyRole("ADMIN", "MODERATOR")   // rule A
                        .requestMatchers("/api/**", "/api/admin/**", "/api/moderation/**").permitAll()  // rule B, contains /api/moderation/** again
//                                .requestMatchers("/api/**","/api/admin/**","/api/moderation/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/recruiter/profile/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")

//                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/code/*"))
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/swagger-ui.html")
                )

                // 5. Your custom authentication & JWT filters
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 5. CORS configuration (crucial for local frontend-backend communication)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Adjust or add ports your teammates use (e.g., 3000, 5173, etc.)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
