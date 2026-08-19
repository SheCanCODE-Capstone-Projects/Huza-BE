package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ConsentRequest;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.ApprovalStatus;
import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.entity.ConsentStatus;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.NotificationType;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.ConsentRepository;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    @Transactional
    public Consent createConsent(String recruiterEmail, ConsentRequest request) {
        // 1. Find the application (from Person 3)
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        // 2. Validate application is ACCEPTED (Person 3's output)
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application must be ACCEPTED before hiring. Current status: " + application.getStatus()
            );
        }

        // 3. Validate recruiter owns this application's job
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recruiter not found"
                ));

        Job job = application.getJob();

        // Check if the recruiter is the one who posted this job
        if (!job.getPostedBy().equals(recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to hire for this job"
            );
        }

        // 4. Check if consent already exists
        boolean consentExists = consentRepository.existsByJobAndArtistAndApprovalStatus(
                job,
                application.getArtist(),
                ApprovalStatus.APPROVED
        );

        if (consentExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A contract already exists for this job and artist"
            );
        }

        // 5. Create the consent
        Consent consent = Consent.builder()
                .job(job)
                .recruiter(recruiter)
                .artist(application.getArtist())
                .agreedSalary(request.getAgreedSalary())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .terms(request.getTerms())
                .specialConditions(request.getSpecialConditions())
                .approvalStatus(ApprovalStatus.PENDING)
                .status(ConsentStatus.DRAFT)
                .build();

        Consent savedConsent = consentRepository.save(consent);

        // 6. Send notifications with NotificationType.CONTRACT
        notificationService.notify(
                application.getArtist().getId(),
                "Contract Ready for Review",
                "You have a new contract from " + recruiter.getFullName() + " for " + job.getTitle(),
                "/contracts/" + savedConsent.getId(),
                NotificationType.CONTRACT  // Using CONTRACT type
        );

        notificationService.notify(
                recruiter.getId(),
                "Contract Created",
                "Contract for " + application.getArtist().getFullName() + " has been created successfully",
                "/contracts/" + savedConsent.getId(),
                NotificationType.CONTRACT  // Using CONTRACT type
        );

        log.info("Contract created: Recruiter {} hired {} for job {}",
                recruiterEmail, application.getArtist().getEmail(), job.getTitle());

        return savedConsent;
    }

    @Transactional(readOnly = true)
    public ConsentResponse getConsentById(String id, String email) {
        Consent consent = consentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consent not found"
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Only recruiter or artist can view
        if (!consent.getRecruiter().getId().equals(user.getId()) &&
                !consent.getArtist().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view this contract"
            );
        }

        return mapToResponse(consent);
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByRecruiter(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recruiter not found"
                ));

        return consentRepository.findByRecruiter(recruiter)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByArtist(String email) {
        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Artist not found"
                ));

        return consentRepository.findByArtist(artist)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConsentResponse mapToResponse(Consent consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .jobId(consent.getJob().getId())
                .jobTitle(consent.getJob().getTitle())
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
                .approvedBy(consent.getApprovedBy())
                .approvedAt(consent.getApprovedAt())
                .build();
    }
}