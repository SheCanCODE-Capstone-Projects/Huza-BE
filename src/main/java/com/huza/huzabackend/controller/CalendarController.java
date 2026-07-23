package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.dto.CalendarRequest;
import com.huza.huzabackend.dto.CalendarResponse;
import com.huza.huzabackend.dto.ScheduleJobRequest;
import com.huza.huzabackend.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/artist/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Artist availability and scheduled job calendar management endpoints")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "Set availability", description = "Set or update availability for a specific date")
    @PostMapping
    public ResponseEntity<ApiResponse<CalendarResponse>> setAvailability(
            @RequestParam(required = false) String artistId,
            @Valid @RequestBody CalendarRequest request) {
        String effectiveArtistId = resolveArtistId(artistId);
        CalendarResponse calendar = calendarService.setAvailability(effectiveArtistId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Availability set successfully")
                        .data(calendar)
                        .build());
    }

    @Operation(summary = "Schedule accepted job", description = "Schedule an accepted job application on the artist calendar for a specific date")
    @PostMapping("/schedule-job")
    public ResponseEntity<ApiResponse<CalendarResponse>> scheduleAcceptedJob(
            @RequestParam(required = false) String artistId,
            @Valid @RequestBody ScheduleJobRequest request) {
        String effectiveArtistId = resolveArtistId(artistId);
        CalendarResponse calendar = calendarService.scheduleAcceptedJob(effectiveArtistId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Accepted job successfully scheduled on calendar")
                        .data(calendar)
                        .build());
    }

    @Operation(summary = "Get accepted jobs for calendar", description = "Retrieve all job applications accepted for the artist that can be scheduled")
    @GetMapping("/accepted-jobs")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAcceptedJobs(
            @RequestParam(required = false) String artistId) {
        String effectiveArtistId = resolveArtistId(artistId);
        List<ApplicationResponse> acceptedJobs = calendarService.getAcceptedJobsForCalendar(effectiveArtistId);

        return ResponseEntity.ok(ApiResponse.<List<ApplicationResponse>>builder()
                .success(true)
                .message("Accepted jobs retrieved successfully")
                .data(acceptedJobs)
                .build());
    }

    @Operation(summary = "View artist availability", description = "Get all availability entries for the artist including day of week")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getArtistAvailability(
            @RequestParam(required = false) String artistId) {
        String effectiveArtistId = resolveArtistId(artistId);
        List<CalendarResponse> availability = calendarService.getArtistAvailability(effectiveArtistId);

        return ResponseEntity.ok(ApiResponse.<List<CalendarResponse>>builder()
                .success(true)
                .message("Availability retrieved successfully")
                .data(availability)
                .build());
    }

    @Operation(summary = "Get availability by date range", description = "Get availability for a specific date range")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getAvailabilityByDateRange(
            @RequestParam(required = false) String artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String effectiveArtistId = resolveArtistId(artistId);
        List<CalendarResponse> availability = calendarService.getAvailabilityByDateRange(effectiveArtistId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.<List<CalendarResponse>>builder()
                .success(true)
                .message("Availability retrieved successfully")
                .data(availability)
                .build());
    }

    @Operation(summary = "Get availability by day of the week", description = "Filter artist availability entries by day of week (e.g. MONDAY, TUESDAY)")
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getAvailabilityByDayOfWeek(
            @RequestParam(required = false) String artistId,
            @PathVariable DayOfWeek dayOfWeek) {
        
        String effectiveArtistId = resolveArtistId(artistId);
        List<CalendarResponse> availability = calendarService.getAvailabilityByDayOfWeek(effectiveArtistId, dayOfWeek);

        return ResponseEntity.ok(ApiResponse.<List<CalendarResponse>>builder()
                .success(true)
                .message("Availability for " + dayOfWeek + " retrieved successfully")
                .data(availability)
                .build());
    }

    @Operation(summary = "Delete availability", description = "Remove availability for a specific date")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(
            @RequestParam(required = false) String artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        String effectiveArtistId = resolveArtistId(artistId);
        calendarService.deleteAvailability(effectiveArtistId, date);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Availability deleted successfully")
                .build());
    }

    /**
     * Resolve artist ID from request param or fallback to SecurityContext authentication
     */
    private String resolveArtistId(String artistIdParam) {
        if (artistIdParam != null && !artistIdParam.isBlank()) {
            return artistIdParam.trim();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        if (authentication != null && authentication.getPrincipal() instanceof String && !authentication.getPrincipal().equals("anonymousUser")) {
            return (String) authentication.getPrincipal();
        }
        throw new RuntimeException("Artist ID is required (pass artistId parameter or authenticate)");
    }
}
