package com.huza.huzabackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(Map.of(
                "principal", auth != null ? auth.getName() : "none",
                "authorities", auth != null ? auth.getAuthorities().toString() : "none",
                "authenticated", auth != null && auth.isAuthenticated(),
                "authClass", auth != null ? auth.getClass().getSimpleName() : "none"
        ));
    }
}