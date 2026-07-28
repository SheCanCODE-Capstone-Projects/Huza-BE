package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long jobId;
    private String recruiterUserId;
    private String recruiterName;
    private Long companyId;
    private String companyName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private String contractType;
    private String experienceLevel;
    private LocalDate deadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}