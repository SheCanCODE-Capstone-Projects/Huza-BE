package com.huza.huzabackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste the JWT returned by the Huza login endpoint.");

        Scopes scopes = new Scopes()
                .addString("openid", "OpenID identifier")
                .addString("email", "Access to your email address")
                .addString("profile", "Access to your profile information");

        SecurityScheme googleOAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Sign in with your Google account")
                .flows(new OAuthFlows()
                        .implicit(new OAuthFlow()
                                .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                                .scopes(scopes)));

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme)
                        .addSecuritySchemes("google_oauth2", googleOAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList("google_oauth2", List.of("openid", "email", "profile")))
                .info(new Info()
                        .title("Huza Authentication Service API")
                        .version("1.0.0")
                        .description("""
                                **Huza Authentication Service**
                                
                                This API provides comprehensive user management and authentication features.
                                
                                ## Features
                                - ✅ User Registration
                                - ✅ Email Verification with OTP
                                - ✅ Profile Management
                                - ✅ User Status Management
                                - ✅ Role-Based Access Control
                                - ✅ JWT Authentication
                                - ✅ OAuth2 Login
                                - ✅ Password Reset with OTP
                                """)
                        .contact(new Contact()
                                .name("Huza Team")
                                .email("support@huza.com")
                                .url("https://huza.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8016")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.huza.com")
                                .description("Production Server")
                ));
    }
}
