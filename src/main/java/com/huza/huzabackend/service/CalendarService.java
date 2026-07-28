package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ApplicationMapper;
import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.dto.CalendarRequest;
import com.huza.huzabackend.dto.CalendarResponse;
import com.huza.huzabackend.dto.ScheduleJobRequest;
import com.huza.huzabackend.entity.*;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.CalendarRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;

    /**
     * Set availability for a specific date
     */
    @Transactional
    public CalendarResponse setAvailability(String artistId, CalendarRequest request) {
        final String cleanedArtistId = artistId.trim();
        log.info("Setting availability for artist {} on date {}", cleanedArtistId, request.getDate());

        User artist = userRepository.findById(cleanedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + cleanedArtistId));

        Calendar calendar = calendarRepository.findByArtistIdAndDate(cleanedArtistId, request.getDate())
                .orElse(Calendar.builder()
                        .artist(artist)
                        .date(request.getDate())
                        .build());

        calendar.setStatus(request.getStatus());
        calendar.setNotes(request.getNotes());
        calendar.setStartTime(request.getStartTime());
        calendar.setEndTime(request.getEndTime());

        Calendar savedCalendar = calendarRepository.save(calendar);
        log.info("Availability set successfully for date: {}", request.getDate());

        return mapToResponse(savedCalendar);
    }

    /**
     * Schedule an accepted job on the artist's calendar
     */
    @Transactional
    public CalendarResponse scheduleAcceptedJob(String artistId, ScheduleJobRequest request) {
        final String cleanedArtistId = artistId.trim();
        final String applicationId = request.getApplicationId().trim();
        log.info("Scheduling accepted job application {} for artist {} on date {}", applicationId, cleanedArtistId, request.getDate());

        Application application = applicationRepository.findByIdAndArtistId(applicationId, cleanedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found or does not belong to artist: " + applicationId));

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("Only ACCEPTED job applications can be scheduled on your calendar");
        }

        User artist = userRepository.findById(cleanedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + cleanedArtistId));

        Calendar calendar = calendarRepository.findByArtistIdAndDate(cleanedArtistId, request.getDate())
                .orElse(Calendar.builder()
                        .artist(artist)
                        .date(request.getDate())
                        .build());

        calendar.setStatus(AvailabilityStatus.BUSY);
        calendar.setJob(application.getJob());
        calendar.setApplication(application);
        calendar.setStartTime(request.getStartTime());
        calendar.setEndTime(request.getEndTime());

        String defaultNotes = "Job Scheduled: " + application.getJob().getTitle();
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            calendar.setNotes(request.getNotes().trim());
        } else {
            calendar.setNotes(defaultNotes);
        }

        Calendar savedCalendar = calendarRepository.save(calendar);
        log.info("Job successfully scheduled on calendar for date: {}", request.getDate());

        return mapToResponse(savedCalendar);
    }

    /**
     * Fetch all accepted job applications for the artist
     */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAcceptedJobsForCalendar(String artistId) {
        final String cleanedArtistId = artistId.trim();
        log.info("Fetching accepted jobs for artist: {}", cleanedArtistId);
        List<Application> acceptedApplications = applicationRepository.findByArtistIdAndStatusWithDetails(cleanedArtistId, ApplicationStatus.ACCEPTED);
        return acceptedApplications.stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    /**
     * Get artist availability
     */
    @Transactional(readOnly = true)
    public List<CalendarResponse> getArtistAvailability(String artistId) {
        final String cleanedArtistId = artistId.trim();
        log.info("Fetching availability for artist: {}", cleanedArtistId);
        return calendarRepository.findByArtistIdOrderByDateAsc(cleanedArtistId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get availability for a date range
     */
    @Transactional(readOnly = true)
    public List<CalendarResponse> getAvailabilityByDateRange(String artistId, LocalDate startDate, LocalDate endDate) {
        final String cleanedArtistId = artistId.trim();
        log.info("Fetching availability for artist {} from {} to {}", cleanedArtistId, startDate, endDate);
        return calendarRepository.findByArtistIdAndDateBetweenOrderByDateAsc(cleanedArtistId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get availability entries filtered by day of the week
     */
    @Transactional(readOnly = true)
    public List<CalendarResponse> getAvailabilityByDayOfWeek(String artistId, DayOfWeek dayOfWeek) {
        final String cleanedArtistId = artistId.trim();
        log.info("Fetching availability for artist {} for day of week {}", cleanedArtistId, dayOfWeek);
        return calendarRepository.findByArtistIdOrderByDateAsc(cleanedArtistId).stream()
                .filter(c -> c.getDate() != null && c.getDate().getDayOfWeek() == dayOfWeek)
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Delete availability for a specific date
     */
    @Transactional
    public void deleteAvailability(String artistId, LocalDate date) {
        final String cleanedArtistId = artistId.trim();
        log.info("Deleting availability for artist {} on date {}", cleanedArtistId, date);

        Calendar calendar = calendarRepository.findByArtistIdAndDate(cleanedArtistId, date)
                .orElseThrow(() -> new ResourceNotFoundException("No availability found for date: " + date));

        calendarRepository.delete(calendar);
        log.info("Availability deleted successfully for date: {}", date);
    }

    private CalendarResponse mapToResponse(Calendar calendar) {
        DayOfWeek dayOfWeek = calendar.getDate() != null ? calendar.getDate().getDayOfWeek() : null;
        Long jobId = calendar.getJob() != null ? calendar.getJob().getJobId() : null;
        String jobTitle = calendar.getJob() != null ? calendar.getJob().getTitle() : null;
        String applicationId = calendar.getApplication() != null ? calendar.getApplication().getId() : null;

        return CalendarResponse.builder()
                .id(calendar.getId())
                .artistId(calendar.getArtist() != null ? calendar.getArtist().getId() : null)
                .date(calendar.getDate())
                .dayOfWeek(dayOfWeek)
                .status(calendar.getStatus())
                .notes(calendar.getNotes())
                .startTime(calendar.getStartTime())
                .endTime(calendar.getEndTime())
                .jobId(jobId)
                .jobTitle(jobTitle)
                .applicationId(applicationId)
                .createdAt(calendar.getCreatedAt())
                .build();
    }
}
