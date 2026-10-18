package com.huza.huzabackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConsentRequest {

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    @NotNull(message = "Agreed salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private BigDecimal agreedSalary;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String terms;

    private String specialConditions;
}