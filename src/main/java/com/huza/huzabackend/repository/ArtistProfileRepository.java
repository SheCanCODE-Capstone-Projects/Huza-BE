package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, String> {
    // Because of @MapsId, findById(userId) queries the primary key directly
}