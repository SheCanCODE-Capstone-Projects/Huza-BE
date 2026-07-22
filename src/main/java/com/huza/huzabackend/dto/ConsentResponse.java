package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConsentResponse {
    private Long consentId;
    private String applicationId;
    private Long jobId;
    private String jobTitle;
    private String artistId;
    private String artistName;
    private String managerId;
    private String managerName;
    private String paymentDuration;
    private String terms;
    private String approvalStatus;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
