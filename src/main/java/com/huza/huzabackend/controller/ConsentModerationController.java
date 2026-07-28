package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.dto.CreateConsentRequest;
import com.huza.huzabackend.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moderation/consents")
@RequiredArgsConstructor
@Tag(name = "Consent Management", description = "Approve, reject, and archive employment consents/contracts by Admin")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public class ConsentModerationController {

    private final ConsentService consentService;

    @PostMapping
    @Operation(summary = "Create a consent for an accepted application")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER', 'MODERATOR')")
    public ResponseEntity<ApiResponse<ConsentResponse>> createConsent(
            @Valid @RequestBody CreateConsentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Consent created and awaiting review", consentService.createConsent(request)));
    }

    @GetMapping("/pending")
    @Operation(summary = "List consents awaiting review")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getPendingConsents() {
        return ResponseEntity.ok(ApiResponse.success(
                "Pending consents retrieved", consentService.getPendingConsents()));
    }

    @PatchMapping("/{consentId}/approve")
    @Operation(summary = "Approve an employment consent by Admin")
    public ResponseEntity<ApiResponse<ConsentResponse>> approveConsent(
            @PathVariable Long consentId,
            @RequestParam(required = false) String adminId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Consent approved", consentService.approveConsent(consentId, adminId)));
    }

    @PatchMapping("/{consentId}/reject")
    @Operation(summary = "Reject a consent by Admin")
    public ResponseEntity<ApiResponse<ConsentResponse>> rejectConsent(
            @PathVariable Long consentId,
            @RequestParam(required = false) String adminId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Consent rejected", consentService.rejectConsent(consentId, adminId)));
    }

    @PatchMapping("/{consentId}/archive")
    @Operation(summary = "Archive a completed contract")
    public ResponseEntity<ApiResponse<ConsentResponse>> archiveConsent(@PathVariable Long consentId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Consent archived", consentService.archiveConsent(consentId)));
    }
}
