package com.huza.huzabackend.dto;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateJobRequest {
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private String contractType;
    private String experienceLevel;

    @Future
    private LocalDate deadline;

    private Long categoryId;
}