package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CreateReviewRequest;
import com.huza.huzabackend.dto.ReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse submitReview(String reviewerEmail, ReviewRequest request);
    ReviewResponse createReview(CreateReviewRequest request);
    List<ReviewResponse> getReviews(String reviewedUserId);
    List<ReviewResponse> getApprovedReviewsForArtist(String artistId);
}