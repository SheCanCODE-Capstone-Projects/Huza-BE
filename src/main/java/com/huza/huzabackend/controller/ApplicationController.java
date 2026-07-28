package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ApplicationRequest;
import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management endpoints for artists")
@PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Apply for a job")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            @RequestParam String artistId,
            @RequestBody ApplicationRequest request) {
        ApplicationResponse application = applicationService.applyForJob(artistId, request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", application));
    }

    @Operation(summary = "Withdraw application")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable String id,
            @RequestParam String artistId) {
        applicationService.withdrawApplication(id, artistId);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully", null));
    }

    @Operation(summary = "View application history")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationHistory(@RequestParam String artistId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application history retrieved successfully",
                applicationService.getApplicationHistory(artistId)));
    }

    @Operation(summary = "Track application details and status")
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationStatus(
            @PathVariable String id,
            @RequestParam String artistId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application status retrieved successfully",
                applicationService.getApplicationStatus(id, artistId)));
    }

    @Operation(summary = "Get one application with its job details")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplication(
            @PathVariable String id,
            @RequestParam String artistId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Application retrieved successfully",
                applicationService.getApplicationById(id, artistId)));
    }
}
