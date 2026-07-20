package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.Application.ApplicationStatus;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class ApplicantSearchRequest {

    private String jobId;
    private String artistId;
    private ApplicationStatus status;

    /** Matches against artist name/email or cover letter */
    private String keyword;

    private Double minProposedRate;
    private Double maxProposedRate;

    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";

    public Pageable toPageable() {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        return PageRequest.of(page, size, sort);
    }
}