package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.CalendarRequest;
import com.huza.huzabackend.entity.Calendar;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/artist/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Artist availability calendar management endpoints")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "Set availability", description = "Set or update availability for a specific date")
    @PostMapping
    public ResponseEntity<ApiResponse<Calendar>> setAvailability(@Valid @RequestBody CalendarRequest request) {
        String artistId = getCurrentUserId();
        Calendar calendar = calendarService.setAvailability(artistId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Calendar>builder()
                        .success(true)
                        .message("Availability set successfully")
                        .data(calendar)
                        .build());
    }

    @Operation(summary = "View artist availability", description = "Get all availability entries for the artist")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Calendar>>> getArtistAvailability() {
        String artistId = getCurrentUserId();
        List<Calendar> availability = calendarService.getArtistAvailability(artistId);

        return ResponseEntity.ok(ApiResponse.<List<Calendar>>builder()
                .success(true)
                .message("Availability retrieved successfully")
                .data(availability)
                .build());
    }

    @Operation(summary = "Get availability by date range", description = "Get availability for a specific date range")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<Calendar>>> getAvailabilityByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String artistId = getCurrentUserId();
        List<Calendar> availability = calendarService.getAvailabilityByDateRange(artistId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.<List<Calendar>>builder()
                .success(true)
                .message("Availability retrieved successfully")
                .data(availability)
                .build());
    }

    @Operation(summary = "Delete availability", description = "Remove availability for a specific date")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        String artistId = getCurrentUserId();
        calendarService.deleteAvailability(artistId, date);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Availability deleted successfully")
                .build());
    }

    /**
     * Helper method to get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // You may need to adjust this to get actual user ID
        }
        throw new RuntimeException("User not authenticated");
    }
}
