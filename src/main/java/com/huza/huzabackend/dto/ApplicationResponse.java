package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private String id;
    private Long jobId;
    private String jobTitle;
    private String jobStatus;
    private String artistId;
    private String artistName;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String notes;
}