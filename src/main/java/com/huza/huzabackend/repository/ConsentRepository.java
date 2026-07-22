package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.entity.ConsentApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository extends JpaRepository<Consent, Long> {

    @Query("SELECT c FROM Consent c " +
            "JOIN FETCH c.application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter rec JOIN FETCH rec.user " +
            "JOIN FETCH a.artist " +
            "LEFT JOIN FETCH c.manager " +
            "WHERE c.consentId = :consentId")
    Optional<Consent> findByIdWithDetails(@Param("consentId") Long consentId);

    @Query("SELECT c FROM Consent c " +
            "JOIN FETCH c.application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.recruiter rec JOIN FETCH rec.user " +
            "JOIN FETCH a.artist " +
            "LEFT JOIN FETCH c.manager " +
            "WHERE c.approvalStatus = :status ORDER BY c.createdAt DESC")
    List<Consent> findAllByApprovalStatusWithDetails(@Param("status") ConsentApprovalStatus status);

    boolean existsByApplication_Id(String applicationId);
}
