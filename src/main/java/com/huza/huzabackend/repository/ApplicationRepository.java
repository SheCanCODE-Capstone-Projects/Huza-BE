package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "JOIN FETCH a.artist " +
            "WHERE a.artist.id = :artistId ORDER BY a.appliedAt DESC")
    List<Application> findByArtistIdOrderByAppliedAtDesc(@Param("artistId") String artistId);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "JOIN FETCH a.artist " +
            "WHERE a.id = :id AND a.artist.id = :artistId")
    Optional<Application> findByIdAndArtistId(@Param("id") String id, @Param("artistId") String artistId);

    boolean existsByJob_JobIdAndArtist_Id(Long jobId, String artistId);

    List<Application> findByArtistIdAndStatus(String artistId, ApplicationStatus status);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "JOIN FETCH a.artist " +
            "WHERE a.artist.id = :artistId AND a.status = :status ORDER BY a.appliedAt DESC")
    List<Application> findByArtistIdAndStatusWithDetails(@Param("artistId") String artistId, @Param("status") ApplicationStatus status);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "JOIN FETCH a.artist " +
            "WHERE j.jobId = :jobId ORDER BY a.appliedAt DESC")
    List<Application> findAllByJobIdWithDetails(@Param("jobId") Long jobId);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter r JOIN FETCH r.user " +
            "JOIN FETCH a.artist " +
            "WHERE a.id = :applicationId")
    Optional<Application> findByIdWithDetails(@Param("applicationId") String applicationId);
}
