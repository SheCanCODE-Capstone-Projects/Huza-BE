package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, String> {
    
    List<Calendar> findByArtistIdAndDateBetweenOrderByDateAsc(String artistId, LocalDate startDate, LocalDate endDate);
    
    Optional<Calendar> findByArtistIdAndDate(String artistId, LocalDate date);
    
    List<Calendar> findByArtistIdOrderByDateAsc(String artistId);

    Optional<Calendar> findByArtistIdAndApplicationId(String artistId, String applicationId);
}
