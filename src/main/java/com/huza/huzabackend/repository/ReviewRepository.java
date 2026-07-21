package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.job j " +
            "JOIN FETCH r.recruiter rec JOIN FETCH rec.user " +
            "JOIN FETCH r.artist a JOIN FETCH a.user " +
            "WHERE r.reviewId = :reviewId")
    Optional<Review> findByIdWithDetails(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.job j " +
            "JOIN FETCH r.recruiter rec JOIN FETCH rec.user " +
            "JOIN FETCH r.artist a JOIN FETCH a.user " +
            "WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Review> findAllByStatusWithDetails(@Param("status") Review.ReviewStatus status);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.job j " +
            "JOIN FETCH r.recruiter rec JOIN FETCH rec.user " +
            "JOIN FETCH r.artist a JOIN FETCH a.user " +
            "WHERE a.id = :artistId AND r.status = :status ORDER BY r.createdAt DESC")
    List<Review> findAllByArtistIdAndStatusWithDetails(
            @Param("artistId") String artistId,
            @Param("status") Review.ReviewStatus status);

    boolean existsByJob_JobIdAndRecruiter_RecruiterId(Long jobId, Long recruiterId);
}
