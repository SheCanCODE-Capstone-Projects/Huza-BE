package com.huza.huzabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

<<<<<<< HEAD
=======
                // 4. Merged authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**", "/login", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()

                        // ADD THIS LINE HERE to let your new endpoints bypass security for public viewing/testing:
                        .requestMatchers("/api/artist/profile/**").permitAll()
                        .requestMatchers("/api/artist/skills/**").permitAll()
                        .requestMatchers("/api/skills/**").permitAll()
                                .requestMatchers("/api/**").permitAll()
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
>>>>>>> bfcd407d60c7fcf431273c54699aef97fe4141e2

        return http.build();
    }
}