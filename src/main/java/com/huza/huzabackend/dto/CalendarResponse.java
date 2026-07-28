package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {
    private String id;
    private String artistId;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private AvailabilityStatus status;
    private String notes;
    private String startTime;
    private String endTime;
    private Long jobId;
    private String jobTitle;
    private String applicationId;
    private LocalDateTime createdAt;
}
