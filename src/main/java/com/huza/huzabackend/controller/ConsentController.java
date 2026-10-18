package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/contracts")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsentResponse>> getConsent(
            Authentication authentication,
            @PathVariable String id) {

        String email = authentication.getName();
        ConsentResponse response = consentService.getConsentById(id, email);

        return ResponseEntity.ok(
                ApiResponse.success("Contract retrieved successfully", response)
        );
    }

    @GetMapping("/recruiter")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getMyConsents(
            Authentication authentication) {

        String email = authentication.getName();
        List<ConsentResponse> responses = consentService.getConsentsByRecruiter(email);

        return ResponseEntity.ok(
                ApiResponse.success("Contracts retrieved successfully", responses)
        );
    }

    @GetMapping("/artist")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getArtistConsents(
            Authentication authentication) {

        String email = authentication.getName();
        List<ConsentResponse> responses = consentService.getConsentsByArtist(email);

        return ResponseEntity.ok(
                ApiResponse.success("Contracts retrieved successfully", responses)
        );
    }
}