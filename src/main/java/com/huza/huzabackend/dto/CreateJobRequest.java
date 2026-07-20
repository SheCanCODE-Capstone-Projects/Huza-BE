package com.huza.huzabackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateJobRequest {

    @NotBlank
    private String recruiterUserId; // TODO: replace with @AuthenticationPrincipal once JWT is wired here

    @NotBlank
    private String title;

    private String description;
    private String location;
    private BigDecimal salary;
    private String contractType;
    private String experienceLevel;

    @Future
    private LocalDate deadline;

    private Long companyId;
    @NotNull(message = "categoryId is required")
    private Long categoryId;
}