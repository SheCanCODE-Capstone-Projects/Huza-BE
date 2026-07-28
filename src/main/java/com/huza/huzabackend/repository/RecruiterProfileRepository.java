package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {

    // Fetch the profile using the User's UUID string instead of the long ID
    @Query("SELECT r FROM RecruiterProfile r JOIN FETCH r.user u WHERE u.id = :userId")
    Optional<RecruiterProfile> findByUserIdWithDetails(@Param("userId") String userId);
}