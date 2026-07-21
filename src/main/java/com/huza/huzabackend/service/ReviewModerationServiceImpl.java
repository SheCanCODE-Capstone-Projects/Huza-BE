package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ReviewMapper;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewModerationServiceImpl implements ReviewModerationService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findAllByStatusWithDetails(Review.ReviewStatus.PENDING).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = findReview(reviewId);
        if (review.getStatus() != Review.ReviewStatus.PENDING) {
            throw new IllegalStateException("Only pending reviews can be approved");
        }

        review.setStatus(Review.ReviewStatus.APPROVED);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void removeReview(Long reviewId) {
        reviewRepository.delete(findReview(reviewId));
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
    }
}
