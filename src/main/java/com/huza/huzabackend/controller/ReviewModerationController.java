package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.service.ReviewModerationService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/moderation/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Moderation", description = "Admin approve or reject recruiter reviews of artists")
public class ReviewModerationController {

    private final ReviewModerationService reviewModerationService;

    @GetMapping
    @Operation(summary = "List reviews awaiting approval")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getPendingReviews() {
        return ResponseEntity.ok(ApiResponse.success(
                "Pending reviews retrieved", reviewModerationService.getPendingReviews()));
    }

    @PatchMapping("/{reviewId}/approve")
    @Operation(summary = "Approve a review and keep it visible")
    public ResponseEntity<ApiResponse<ReviewResponse>> approveReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Review approved", reviewModerationService.approveReview(reviewId)));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Reject and remove an inappropriate review")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Review removed")
    public ResponseEntity<Void> removeReview(@PathVariable Long reviewId) {
        reviewModerationService.removeReview(reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
