package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ConsentResponse {
    private String id;
    private String jobId;  // Changed from Long to String since Job uses String ID
    private String jobTitle;
    private String recruiterId;
    private String recruiterName;
    private String artistId;
    private String artistName;
    private String artistEmail;
    private BigDecimal agreedSalary;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String terms;
    private String specialConditions;
    private String approvalStatus;  // PENDING, APPROVED, REJECTED
    private String status;  // DRAFT, SENT, etc.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
}