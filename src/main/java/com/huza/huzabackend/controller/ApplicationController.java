package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ApplicationRequest;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management endpoints for artists")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Apply for a job", description = "Submit an application for a job posting")
    @PostMapping
    public ResponseEntity<ApiResponse<Application>> applyForJob(@Valid @RequestBody ApplicationRequest request) {
        String artistId = getCurrentUserId();
        Application application = applicationService.applyForJob(artistId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Application>builder()
                        .success(true)
                        .message("Application submitted successfully")
                        .data(application)
                        .build());
    }

    @Operation(summary = "Withdraw application", description = "Withdraw a pending application")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(@PathVariable String id) {
        String artistId = getCurrentUserId();
        applicationService.withdrawApplication(id, artistId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Application withdrawn successfully")
                .build());
    }

    @Operation(summary = "View application history", description = "Get all applications submitted by the artist")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Application>>> getApplicationHistory() {
        String artistId = getCurrentUserId();
        List<Application> applications = applicationService.getApplicationHistory(artistId);

        return ResponseEntity.ok(ApiResponse.<List<Application>>builder()
                .success(true)
                .message("Application history retrieved successfully")
                .data(applications)
                .build());
    }

    @Operation(summary = "Track application status", description = "Get the current status of an application")
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationStatus>> getApplicationStatus(@PathVariable String id) {
        ApplicationStatus status = applicationService.getApplicationStatus(id);

        return ResponseEntity.ok(ApiResponse.<ApplicationStatus>builder()
                .success(true)
                .message("Application status retrieved successfully")
                .data(status)
                .build());
    }

    /**
     * Helper method to get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            // Assuming username is the email, and you have a way to get user ID from email
            return userDetails.getUsername(); // You may need to adjust this to get actual user ID
        }
        throw new RuntimeException("User not authenticated");
    }
}
