package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long jobId;
    private String jobTitle;
    private String recruiterUserId;
    private String recruiterName;
    private String artistId;
    private String artistName;
    private Integer rating;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
