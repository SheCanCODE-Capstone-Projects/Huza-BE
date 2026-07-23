package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.ApprovalStatus;
import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, String> {

    List<Consent> findByRecruiter(User recruiter);

    List<Consent> findByArtist(User artist);

    List<Consent> findByJob(Job job);

    Optional<Consent> findByJobAndArtist(Job job, User artist);

    List<Consent> findByRecruiterAndApprovalStatus(User recruiter, ApprovalStatus approvalStatus);

    List<Consent> findByArtistAndApprovalStatus(User artist, ApprovalStatus approvalStatus);

    boolean existsByJobAndArtistAndApprovalStatus(Job job, User artist, ApprovalStatus approvalStatus);
}