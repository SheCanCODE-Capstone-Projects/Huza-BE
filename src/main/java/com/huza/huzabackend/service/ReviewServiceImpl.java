package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ReviewMapper;
import com.huza.huzabackend.dto.CreateReviewRequest;
import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.ArtistProfile;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.RecruiterProfile;
import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ArtistProfileRepository;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.repository.RecruiterProfileRepository;
import com.huza.huzabackend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserIdWithDetails(request.getRecruiterUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recruiter profile not found for user: " + request.getRecruiterUserId()));

        Job job = jobRepository.findByIdWithDetails(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + request.getJobId()));

        if (job.getStatus() != Job.JobStatus.CLOSED) {
            throw new IllegalStateException("Reviews can only be submitted after the job is completed (CLOSED)");
        }

        if (!job.getRecruiter().getRecruiterId().equals(recruiter.getRecruiterId())) {
            throw new IllegalStateException("Only the job's recruiter can review the artist for this job");
        }

        ArtistProfile artist = artistProfileRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + request.getArtistId()));

        if (reviewRepository.existsByJob_JobIdAndRecruiter_RecruiterId(job.getJobId(), recruiter.getRecruiterId())) {
            throw new DuplicateResourceException("A review for this job by this recruiter already exists");
        }

        Review review = Review.builder()
                .job(job)
                .recruiter(recruiter)
                .artist(artist)
                .rating(request.getRating())
                .comment(request.getComment())
                .status(Review.ReviewStatus.PENDING)
                .build();

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedReviewsForArtist(String artistId) {
        if (!artistProfileRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist not found: " + artistId);
        }

        return reviewRepository
                .findAllByArtistIdAndStatusWithDetails(artistId, Review.ReviewStatus.APPROVED)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
