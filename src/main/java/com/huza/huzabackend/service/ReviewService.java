package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ReviewRequest;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.ReviewRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public Review submitReview(String reviewerEmail,
                               ReviewRequest request){

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

        return reviewRepository.save(review);
    }

    public List<Review> getReviews(String reviewedUserId){

        User user = userRepository.findById(reviewedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reviewRepository.findByReviewedUser(user);
    }
}