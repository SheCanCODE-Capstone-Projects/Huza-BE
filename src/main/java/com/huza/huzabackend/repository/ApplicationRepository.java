package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    List<Application> findByArtistIdOrderByAppliedAtDesc(String artistId);

    Optional<Application> findByIdAndArtistId(String id, String artistId);

    boolean existsByJob_JobIdAndArtist_Id(Long jobId, String artistId);

    List<Application> findByArtistIdAndStatus(String artistId, ApplicationStatus status);
}
