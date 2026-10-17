package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarRequest {
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Status is required")
    private AvailabilityStatus status;
    
    private String notes;
    
    private String startTime;
    
    private String endTime;
}
