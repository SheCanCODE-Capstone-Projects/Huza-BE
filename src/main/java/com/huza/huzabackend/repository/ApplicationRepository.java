package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.entity.Application.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

    /**
     * Find all applications for a specific job
     */
    Page<Application> findByJob(Job job, Pageable pageable);

    /**
     * Find all applications by an artist
     */
    List<Application> findByArtist(User artist);

    /**
     * Find application by job and artist
     */
    Optional<Application> findByJobAndArtist(Job job, User artist);

    /**
     * Check if artist already applied to job
     */
    boolean existsByJobAndArtist(Job job, User artist);

    /**
     * Count applications by job
     */
    long countByJob(Job job);

    /**
     * Count applications by status
     */
    long countByStatus(ApplicationStatus status);

    /**
     * Count applications by job and status
     */
    long countByJobAndStatus(Job job, ApplicationStatus status);

    /**
     * Find applications with pagination by status
     */
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    /**
     * Find applications by artist with pagination
     */
    Page<Application> findByArtist(User artist, Pageable pageable);

    /**
     * Find applications by job and status with pagination
     */
    Page<Application> findByJobAndStatus(Job job, ApplicationStatus status, Pageable pageable);

    /**
     * Find applications for a recruiter's jobs
     */
    @Query("SELECT a FROM Application a WHERE a.job.recruiter.id = :recruiterId")
    Page<Application> findByRecruiterId(@Param("recruiterId") String recruiterId, Pageable pageable);

    /**
     * Find applications for a recruiter's jobs with status filter
     */
    @Query("SELECT a FROM Application a WHERE a.job.recruiter.id = :recruiterId AND a.status = :status")
    Page<Application> findByRecruiterIdAndStatus(
            @Param("recruiterId") String recruiterId,
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );

    /**
     * Get distinct applicants for a recruiter
     */
    @Query("SELECT DISTINCT a.artist FROM Application a WHERE a.job.recruiter.id = :recruiterId")
    Page<User> findDistinctApplicantsByRecruiterId(@Param("recruiterId") String recruiterId, Pageable pageable);
}