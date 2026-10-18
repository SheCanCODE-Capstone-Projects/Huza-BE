package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.ReviewRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewResponse submitReview(String reviewerEmail,
                                       ReviewRequest request) {

        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        User reviewed = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new RuntimeException("Reviewed user not found"));

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewedUser(reviewed)
                .rating(request.getRating())
                .comment(request.getComment())
                .consentId(request.getConsentId())
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(saved.getId())
                .reviewerId(reviewer.getId())
                .reviewerName(reviewer.getFullName())
                .reviewerEmail(reviewer.getEmail())
                .reviewedUserId(reviewed.getId())
                .reviewedUserName(reviewed.getFullName())
                .reviewedUserEmail(reviewed.getEmail())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .consentId(saved.getConsentId())
                .createdAt(saved.getCreatedAt())
                .build();
    }
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(String reviewedUserId){

        User user = userRepository.findById(reviewedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reviewRepository.findByReviewedUser(user)
                .stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .reviewerId(review.getReviewer().getId())
                        .reviewerName(review.getReviewer().getFullName())
                        .reviewerEmail(review.getReviewer().getEmail())
                        .reviewedUserId(review.getReviewedUser().getId())
                        .reviewedUserName(review.getReviewedUser().getFullName())
                        .reviewedUserEmail(review.getReviewedUser().getEmail())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .consentId(review.getConsentId())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }
}