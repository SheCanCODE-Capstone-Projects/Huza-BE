package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

    List<Application> findByArtist_IdOrderByCreatedAtDesc(String artistId);

    Optional<Application> findByIdAndArtist_Id(String id, String artistId);

    boolean existsByJob_IdAndArtist_Id(String jobId, String artistId);

    List<Application> findByArtist_IdAndStatus(String artistId, ApplicationStatus status);

    Page<Application> findByJob(Job job, Pageable pageable);

    long countByJob(Job job);

    long countByJobAndStatus(Job job, ApplicationStatus status);
}
