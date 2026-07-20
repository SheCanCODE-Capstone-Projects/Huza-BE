package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ApplicantResponse;
import com.huza.huzabackend.dto.ApplicantSearchRequest;
import com.huza.huzabackend.entity.Portfolio;
import com.huza.huzabackend.service.ApplicantSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/applicants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Applicant Discovery", description = "APIs for recruiters to discover and manage applicants")
public class ApplicantController {

    private final ApplicantSearchService applicantSearchService;

    /**
     * Get all applicants for a specific job
     */
    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get applicants for a job",
            description = "Retrieves all applicants for a specific job with pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<Page<ApplicantResponse>>> getApplicantsForJob(
            @Parameter(description = "Job ID", required = true)
            @PathVariable String jobId,

            @Parameter(description = "Page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (default: 20)")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort by field (default: createdAt)")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (ASC/DESC, default: DESC)")
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("📋 Fetching applicants for job: {}", jobId);

        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<ApplicantResponse> applicants = applicantSearchService.getApplicantsForJob(jobId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Applicants retrieved successfully", applicants));

        } catch (Exception e) {
            log.error("❌ Failed to fetch applicants: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch applicants: " + e.getMessage()));
        }
    }

    /**
     * Search applicants with dynamic filters
     */
    @PostMapping("/search")
    @Operation(summary = "Search applicants",
            description = "Search and filter applicants by keyword, skills, location, experience, and more")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<Page<ApplicantResponse>>> searchApplicants(
            @Valid @RequestBody ApplicantSearchRequest request) {

        log.info("🔍 Searching applicants with filters: {}", request);

        try {
            Page<ApplicantResponse> applicants = applicantSearchService.searchApplicants(request);
            return ResponseEntity.ok(ApiResponse.success("Applicants found", applicants));

        } catch (Exception e) {
            log.error("❌ Applicant search failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Applicant search failed: " + e.getMessage()));
        }
    }

    /**
     * Search applicants for a recruiter's jobs
     */
    @PostMapping("/search/my")
    @Operation(summary = "Search applicants for recruiter",
            description = "Search and filter applicants who applied to jobs posted by this recruiter")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<Page<ApplicantResponse>>> searchMyApplicants(
            @RequestParam String recruiterId,
            @Valid @RequestBody ApplicantSearchRequest request) {

        log.info("🔍 Searching applicants for recruiter: {}", recruiterId);

        try {
            Page<ApplicantResponse> applicants = applicantSearchService.searchApplicantsForRecruiter(
                    recruiterId,
                    request
            );
            return ResponseEntity.ok(ApiResponse.success("Applicants found", applicants));

        } catch (Exception e) {
            log.error("❌ Applicant search failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Applicant search failed: " + e.getMessage()));
        }
    }

    /**
     * Get applicant portfolio
     */
    @GetMapping("/{artistId}/portfolio")
    @Operation(summary = "Get applicant portfolio",
            description = "Retrieves portfolio items for a specific applicant")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<List<Portfolio>>> getApplicantPortfolio(
            @Parameter(description = "Artist ID", required = true)
            @PathVariable String artistId) {

        log.info("📁 Fetching portfolio for artist: {}", artistId);

        try {
            List<Portfolio> portfolio = applicantSearchService.getApplicantPortfolio(artistId);
            return ResponseEntity.ok(ApiResponse.success("Portfolio retrieved successfully", portfolio));

        } catch (Exception e) {
            log.error("❌ Failed to fetch portfolio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch portfolio: " + e.getMessage()));
        }
    }

    /**
     * Get applicant details
     */
    @GetMapping("/{applicationId}/details")
    @Operation(summary = "Get applicant details",
            description = "Retrieves detailed information about a specific applicant")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicantResponse>> getApplicantDetails(
            @Parameter(description = "Application ID", required = true)
            @PathVariable String applicationId) {

        log.info("👤 Fetching applicant details for application: {}", applicationId);

        try {
            ApplicantResponse applicant = applicantSearchService.getApplicantDetails(applicationId);
            return ResponseEntity.ok(ApiResponse.success("Applicant details retrieved", applicant));

        } catch (Exception e) {
            log.error("❌ Failed to fetch applicant details: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch applicant details: " + e.getMessage()));
        }
    }

    /**
     * Get application statistics for a job
     */
    @GetMapping("/jobs/{jobId}/statistics")
    @Operation(summary = "Get application statistics",
            description = "Retrieves application statistics (total, pending, accepted, rejected, etc.)")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicantSearchService.ApplicationStatistics>> getApplicationStatistics(
            @Parameter(description = "Job ID", required = true)
            @PathVariable String jobId) {

        log.info("📊 Fetching application statistics for job: {}", jobId);

        try {
            ApplicantSearchService.ApplicationStatistics stats =
                    applicantSearchService.getApplicationStatistics(jobId);
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", stats));

        } catch (Exception e) {
            log.error("❌ Failed to fetch statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch statistics: " + e.getMessage()));
        }
    }
}