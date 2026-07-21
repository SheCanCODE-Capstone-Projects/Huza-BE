package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.service.JobModerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moderation/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Moderation", description = "Review and moderate job listings awaiting approval")
@SecurityRequirement(name = "bearerAuth")
public class JobModerationController {

    private final JobModerationService jobModerationService;

    @GetMapping("/pending")
    @Operation(summary = "List jobs awaiting approval")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getPendingJobs() {
        return ResponseEntity.ok(ApiResponse.success(
                "Pending jobs retrieved", jobModerationService.getPendingJobs()));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job detail for moderation review")
    public ResponseEntity<ApiResponse<JobResponse>> getJobForReview(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Job retrieved for moderation", jobModerationService.getJobForReview(jobId)));
    }

    @PatchMapping("/{jobId}/approve")
    @Operation(summary = "Approve a pending job listing")
    public ResponseEntity<ApiResponse<JobResponse>> approveJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Job approved", jobModerationService.approveJob(jobId)));
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "Remove an inappropriate job listing")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Job removed")
    public ResponseEntity<Void> removeJob(@PathVariable Long jobId) {
        jobModerationService.removeJob(jobId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
