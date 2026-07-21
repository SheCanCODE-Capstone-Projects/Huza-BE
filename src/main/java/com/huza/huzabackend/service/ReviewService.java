package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CreateReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(CreateReviewRequest request);

    List<ReviewResponse> getApprovedReviewsForArtist(String artistId);
}
