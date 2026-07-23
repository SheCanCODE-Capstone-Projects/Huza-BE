package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ReviewMapper;
import com.huza.huzabackend.dto.CreateReviewRequest;
import com.huza.huzabackend.dto.ReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.ApprovalStatus;
import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ConsentRepository;
import com.huza.huzabackend.repository.ReviewRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ConsentRepository consentRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse submitReview(String reviewerEmail, ReviewRequest request) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        User reviewed = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new RuntimeException("Reviewed user not found"));

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewedUser(reviewed)
                .rating(request.getRating())
                .comment(request.getComment())
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
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reviewer not found: " + request.getReviewerId()));

        Consent consent = consentRepository.findById(String.valueOf(request.getConsentId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consent not found: " + request.getConsentId()));

        if (consent.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Reviews require an approved consent");
        }

        Job job = consent.getJob();
        if (job != null && job.getStatus() != Job.JobStatus.CLOSED) {
            throw new IllegalStateException("Reviews can only be submitted after the job is completed (CLOSED)");
        }

        String jobRecruiterUserId = consent.getRecruiter().getId();
        if (!jobRecruiterUserId.equals(reviewer.getId())) {
            throw new IllegalStateException("Only the job recruiter can review the artist for this consent");
        }

        User reviewedUser = consent.getArtist();

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)
                .consentId(consent.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(String reviewedUserId) {
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
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedReviewsForArtist(String artistId) {
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + artistId));

        return reviewRepository.findByReviewedUser(user)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
