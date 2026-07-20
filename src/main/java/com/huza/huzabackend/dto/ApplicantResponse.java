package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.Application.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Applicant response for recruiters")
public class ApplicantResponse {

    @Schema(description = "Application ID")
    private String applicationId;

    @Schema(description = "Artist ID")
    private String artistId;

    @Schema(description = "Artist full name")
    private String artistName;

    @Schema(description = "Artist email")
    private String artistEmail;

    @Schema(description = "Artist profile picture")
    private String profilePicture;

    @Schema(description = "Artist location")
    private String location;

    @Schema(description = "Artist headline")
    private String headline;

    @Schema(description = "Artist experience years")
    private Integer experienceYears;

    @Schema(description = "Artist skills")
    private List<String> skills;

    @Schema(description = "Cover letter")
    private String coverLetter;

    @Schema(description = "Proposed rate")
    private Double proposedRate;

    @Schema(description = "Application status")
    private ApplicationStatus status;

    @Schema(description = "Application submitted date")
    private LocalDateTime appliedAt;

    @Schema(description = "Last updated")
    private LocalDateTime updatedAt;

    @Schema(description = "Portfolio count")
    private Integer portfolioCount;

    @Schema(description = "Rating average")
    private Double averageRating;
}