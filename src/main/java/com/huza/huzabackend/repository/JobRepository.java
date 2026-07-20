package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "LEFT JOIN FETCH j.company LEFT JOIN FETCH j.category WHERE j.jobId = :jobId")
    Optional<Job> findByIdWithDetails(@Param("jobId") Long jobId);

    @Query("SELECT j FROM Job j WHERE j.recruiter.user.id = :userId ORDER BY j.createdAt DESC")
    List<Job> findAllByRecruiterUserId(@Param("userId") String userId);

    List<Job> findAllByStatusAndDeadlineBefore(Job.JobStatus status, LocalDate date);

    // NEW — every job, most recent first, with associations pre-fetched
    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "LEFT JOIN FETCH j.company LEFT JOIN FETCH j.category ORDER BY j.createdAt DESC")
    List<Job> findAllWithDetails();

    // NEW — same, but filtered to a status (e.g. only OPEN jobs for public browsing)
    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "LEFT JOIN FETCH j.company LEFT JOIN FETCH j.category " +
            "WHERE j.status = :status ORDER BY j.createdAt DESC")
    List<Job> findAllByStatusWithDetails(@Param("status") Job.JobStatus status);
}