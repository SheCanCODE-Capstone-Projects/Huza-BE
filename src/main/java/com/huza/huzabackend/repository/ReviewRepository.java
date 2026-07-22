package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.reviewer " +
            "JOIN FETCH r.reviewedUser " +
            "JOIN FETCH r.consent c " +
            "JOIN FETCH c.application a " +
            "JOIN FETCH a.job " +
            "WHERE r.reviewId = :reviewId")
    Optional<Review> findByIdWithDetails(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.reviewer " +
            "JOIN FETCH r.reviewedUser " +
            "JOIN FETCH r.consent c " +
            "JOIN FETCH c.application a " +
            "JOIN FETCH a.job " +
            "WHERE r.moderationStatus = :status ORDER BY r.reviewDate DESC")
    List<Review> findAllByModerationStatusWithDetails(@Param("status") Review.ModerationStatus status);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.reviewer " +
            "JOIN FETCH r.reviewedUser " +
            "JOIN FETCH r.consent c " +
            "JOIN FETCH c.application a " +
            "JOIN FETCH a.job " +
            "WHERE r.reviewedUser.id = :reviewedUserId AND r.moderationStatus = :status " +
            "ORDER BY r.reviewDate DESC")
    List<Review> findAllByReviewedUserIdAndModerationStatusWithDetails(
            @Param("reviewedUserId") String reviewedUserId,
            @Param("status") Review.ModerationStatus status);

    boolean existsByConsent_ConsentId(Long consentId);
}
