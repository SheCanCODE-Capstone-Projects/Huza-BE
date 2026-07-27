package com.huza.huzabackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void shouldExposeBearerAuthScheme() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("bearerAuth"));
    }
}