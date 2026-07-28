package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleJobRequest {

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String startTime;

    private String endTime;

    private String notes;
}
