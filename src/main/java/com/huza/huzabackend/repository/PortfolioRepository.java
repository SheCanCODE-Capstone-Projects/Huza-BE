package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Portfolio;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

    List<Portfolio> findByArtist(User artist);

    List<Portfolio> findByArtistIdAndIsFeaturedTrue(String artistId);

    Optional<Portfolio> findByIdAndArtistId(String id, String artistId);

    long countByArtistId(String artistId);
}