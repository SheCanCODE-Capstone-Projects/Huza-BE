package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.CreateReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/reviews")
@RequiredArgsConstructor
@Tag(name = "Recruiter Reviews", description = "Recruiters submit reviews for artists after a job is completed")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a review for an artist after the job is completed")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Review submitted and awaiting admin approval", reviewService.createReview(request)));
    }

    @GetMapping("/artist/{artistId}")
    @Operation(summary = "List approved reviews for an artist")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getApprovedReviewsForArtist(
            @PathVariable String artistId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Approved reviews retrieved", reviewService.getApprovedReviewsForArtist(artistId)));
    }
}
