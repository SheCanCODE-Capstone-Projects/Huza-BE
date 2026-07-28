package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ReviewResponse;

import java.util.List;

public interface ReviewModerationService {

    List<ReviewResponse> getPendingReviews();

    ReviewResponse approveReview(Long reviewId);

    void removeReview(Long reviewId);
}
