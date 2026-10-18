package com.huza.huzabackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerConfigTest {

    @Test
    void shouldExposeGoogleOAuthScheme() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();

        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("google_oauth2"));
    }
}
