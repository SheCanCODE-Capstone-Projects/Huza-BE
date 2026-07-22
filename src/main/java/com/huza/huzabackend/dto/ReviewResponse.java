package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String reviewerId;
    private String reviewerName;
    private String reviewedUserId;
    private String reviewedUserName;
    private Long consentId;
    private Long jobId;
    private String jobTitle;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    private String moderationStatus;
}
