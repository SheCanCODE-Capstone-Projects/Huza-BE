package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ConsentRequest;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.service.HiringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
public class HiringController {

    private final HiringService hiringService;

    @PostMapping("/hire")
    public ResponseEntity<ApiResponse<ConsentResponse>> hireArtist(
            Authentication authentication,
            @Valid @RequestBody ConsentRequest request) {

        String email = authentication.getName();
        Consent consent = hiringService.hireArtist(email, request);

        ConsentResponse response = ConsentResponse.builder()
                .id(consent.getId())
                .jobId(consent.getJob() != null && consent.getJob().getJobId() != null ? String.valueOf(consent.getJob().getJobId()) : null)
                .jobTitle(consent.getJob() != null ? consent.getJob().getTitle() : null)
                .recruiterId(consent.getRecruiter().getId())
                .recruiterName(consent.getRecruiter().getFullName())
                .artistId(consent.getArtist().getId())
                .artistName(consent.getArtist().getFullName())
                .artistEmail(consent.getArtist().getEmail())
                .agreedSalary(consent.getAgreedSalary())
                .startDate(consent.getStartDate())
                .endDate(consent.getEndDate())
                .terms(consent.getTerms())
                .specialConditions(consent.getSpecialConditions())
                .approvalStatus(consent.getApprovalStatus().name())
                .status(consent.getStatus().name())
                .createdAt(consent.getCreatedAt())
                .updatedAt(consent.getUpdatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hiring initiated successfully", response));
    }
}