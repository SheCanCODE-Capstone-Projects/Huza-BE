package com.huza.huzabackend.controller;


import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ReviewRequest;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/artist/reviews")
@RequiredArgsConstructor
public class ReviewController {


    private final ReviewService reviewService;



    @GetMapping("/{reviewedUserId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviews(
            @PathVariable String reviewedUserId) {


        List<Review> reviews =
                reviewService.getReviews(reviewedUserId);



        return ResponseEntity.ok(
                ApiResponse.success(
                        "Reviews retrieved successfully",
                        reviews
                )
        );
    }





    @PostMapping
    public ResponseEntity<ApiResponse<Review>> submitReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {


        String reviewerEmail = authentication.getName();



        Review review =
                reviewService.submitReview(
                        reviewerEmail,
                        request
                );



        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Review submitted successfully",
                                review
                        )
                );
    }

}