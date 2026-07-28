package com.huza.huzabackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@Tag(name = "Third-Party Authentication", description = "Sign in with an external identity provider")
public class OAuth2RedirectController {

    @GetMapping("/login")
    public RedirectView loginWithGoogle() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @GetMapping("/api/auth/oauth2/google")
    @Operation(
            summary = "Sign in with Google",
            description = "Returns the browser URL that starts the Google OAuth2 authorization flow. "
                    + "Copy the returned `authorizationUrl` into a browser to sign in. After a successful sign-in, "
                    + "Huza returns a JSON response containing the JWT and the user's role.")
    @SecurityRequirements
    @ApiResponse(
            responseCode = "200",
            description = "URL that starts Google sign-in in a browser")
    public ResponseEntity<Map<String, String>> googleAuthorizationUrl() {
        String authorizationUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/google")
                .toUriString();

        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
    }
}
