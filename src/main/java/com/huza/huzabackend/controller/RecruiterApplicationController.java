package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.dto.AssignApplicantsRequest;
import com.huza.huzabackend.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/applications")
@RequiredArgsConstructor
@Tag(name = "Recruiter Applications", description = "Recruiters accept or manage applications")
public class RecruiterApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/job/{jobId}")
    @Operation(summary = "View all applicants for one job")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicantsForJob(
            @PathVariable Long jobId,
            @RequestParam String recruiterUserId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Applicants retrieved successfully",
                applicationService.getApplicantsForJob(jobId, recruiterUserId)));
    }

    @PatchMapping("/job/{jobId}/assign")
    @Operation(summary = "Assign job to one or two artists, reject remaining pending applicants, and close the job")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> assignJobToApplicants(
            @PathVariable Long jobId,
            @Valid @RequestBody AssignApplicantsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Job assigned and other pending applicants rejected",
                applicationService.assignJobToApplicants(jobId, request.getRecruiterUserId(), request.getSelectedApplicationIds())));
    }
}
