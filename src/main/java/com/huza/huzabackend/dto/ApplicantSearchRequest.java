package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.Application.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@Schema(description = "Applicant search and filter request")
public class ApplicantSearchRequest {

    @Schema(description = "Search keyword (name, email)", example = "John")
    private String keyword;

    @Schema(description = "Filter by skills", example = "Java,Spring")
    private List<String> skills;

    @Schema(description = "Filter by location", example = "Kigali")
    private String location;

    @Schema(description = "Minimum experience years", example = "2")
    private Integer minExperience;

    @Schema(description = "Maximum experience years", example = "10")
    private Integer maxExperience;

    @Schema(description = "Filter by application status", example = "PENDING")
    private ApplicationStatus status;

    @Schema(description = "Filter by minimum rating", example = "4.0")
    private Double minRating;

    @Schema(description = "Filter by availability", example = "true")
    private Boolean available;

    @Schema(description = "Page number", example = "0")
    private int page = 0;

    @Schema(description = "Page size", example = "20")
    private int size = 20;

    @Schema(description = "Sort by field", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Sort direction", example = "DESC")
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}