package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private String id;

    private String reviewerId;
    private String reviewerName;
    private String reviewerEmail;

    private String reviewedUserId;
    private String reviewedUserName;
    private String reviewedUserEmail;

    private Integer rating;
    private String comment;
    private String consentId;

    private LocalDateTime createdAt;
}