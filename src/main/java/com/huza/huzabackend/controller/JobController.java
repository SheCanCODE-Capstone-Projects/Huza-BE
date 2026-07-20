package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.CreateJobRequest;
import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.dto.UpdateJobRequest;
import com.huza.huzabackend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request));
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long jobId, @Valid @RequestBody UpdateJobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobResponse> closeJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.closeJob(jobId));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @GetMapping("/recruiter/{recruiterUserId}")
    public ResponseEntity<List<JobResponse>> getJobsByRecruiter(@PathVariable String recruiterUserId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterUserId));
    }
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs(
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(jobService.getAllJobs(status));
    }
}