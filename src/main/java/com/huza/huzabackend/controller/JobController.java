package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.JobSearchRequest;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.service.JobSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Search", description = "Job search and filtering endpoints for artists")
public class JobController {

    private final JobSearchService jobSearchService;

    @Operation(summary = "Search and filter jobs", description = "Search for jobs with optional filters: category, location, salary, experience level, and contract type")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Job>>> searchJobs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) java.math.BigDecimal salary,
            @RequestParam(required = false) com.huza.huzabackend.entity.ExperienceLevel experienceLevel,
            @RequestParam(required = false) com.huza.huzabackend.entity.ContractType contractType) {

        JobSearchRequest searchRequest = new JobSearchRequest();
        searchRequest.setCategory(category);
        searchRequest.setLocation(location);
        searchRequest.setSalary(salary);
        searchRequest.setExperienceLevel(experienceLevel);
        searchRequest.setContractType(contractType);

        List<Job> jobs = jobSearchService.searchJobs(searchRequest);

        return ResponseEntity.ok(ApiResponse.<List<Job>>builder()
                .success(true)
                .message("Jobs retrieved successfully")
                .data(jobs)
                .build());
    }

    @Operation(summary = "Get job details", description = "Get detailed information about a specific job")
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Job>> getJobById(@PathVariable String jobId) {
        Job job = jobSearchService.getJobById(jobId);

        return ResponseEntity.ok(ApiResponse.<Job>builder()
                .success(true)
                .message("Job details retrieved successfully")
                .data(job)
                .build());
    }
}
