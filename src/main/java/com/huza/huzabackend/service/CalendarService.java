package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CalendarRequest;
import com.huza.huzabackend.entity.Calendar;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.CalendarRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    /**
     * Set availability for a specific date
     */
    @Transactional
    public Calendar setAvailability(String artistId, CalendarRequest request) {
        log.info("Setting availability for artist {} on date {}", artistId, request.getDate());

        // Check if artist exists
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        // Check if availability already exists for this date
        Calendar calendar = calendarRepository.findByArtistIdAndDate(artistId, request.getDate())
                .orElse(Calendar.builder()
                        .artist(artist)
                        .date(request.getDate())
                        .build());

        // Update availability
        calendar.setStatus(request.getStatus());
        calendar.setNotes(request.getNotes());
        calendar.setStartTime(request.getStartTime());
        calendar.setEndTime(request.getEndTime());

        Calendar savedCalendar = calendarRepository.save(calendar);
        log.info("Availability set successfully for date: {}", request.getDate());

        return savedCalendar;
    }

    /**
     * Get artist availability
     */
    @Transactional(readOnly = true)
    public List<Calendar> getArtistAvailability(String artistId) {
        log.info("Fetching availability for artist: {}", artistId);
        return calendarRepository.findByArtistIdOrderByDateAsc(artistId);
    }

    /**
     * Get availability for a date range
     */
    @Transactional(readOnly = true)
    public List<Calendar> getAvailabilityByDateRange(String artistId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching availability for artist {} from {} to {}", artistId, startDate, endDate);
        return calendarRepository.findByArtistIdAndDateBetweenOrderByDateAsc(artistId, startDate, endDate);
    }

    /**
     * Delete availability for a specific date
     */
    @Transactional
    public void deleteAvailability(String artistId, LocalDate date) {
        log.info("Deleting availability for artist {} on date {}", artistId, date);
        
        Calendar calendar = calendarRepository.findByArtistIdAndDate(artistId, date)
                .orElseThrow(() -> new ResourceNotFoundException("No availability found for date: " + date));
        
        calendarRepository.delete(calendar);
        log.info("Availability deleted successfully for date: {}", date);
    }
}
