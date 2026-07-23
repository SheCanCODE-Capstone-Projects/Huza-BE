package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ConsentMapper;
import com.huza.huzabackend.dto.ConsentRequest;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.dto.CreateConsentRequest;
import com.huza.huzabackend.entity.*;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;
    private final ConsentMapper consentMapper;

    @Override
    @Transactional
    public Consent createConsent(String recruiterEmail, ConsentRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application must be ACCEPTED before hiring. Current status: " + application.getStatus()
            );
        }

        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recruiter not found"
                ));

        Job job = application.getJob();

        if (!job.getRecruiter().getUser().getId().equals(recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to hire for this job"
            );
        }

        User artist = application.getArtist();

        boolean consentExists = consentRepository.findByJobAndArtist(job, artist).isPresent();
        if (consentExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A contract already exists for this job and artist"
            );
        }

        BigDecimal agreedSalary = request.getAgreedSalary() != null ? request.getAgreedSalary() : job.getSalary();
        LocalDateTime startDate = request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now();

        Consent consent = Consent.builder()
                .job(job)
                .recruiter(recruiter)
                .artist(artist)
                .agreedSalary(agreedSalary)
                .startDate(startDate)
                .endDate(request.getEndDate())
                .terms(request.getTerms())
                .specialConditions(request.getSpecialConditions())
                .approvalStatus(ApprovalStatus.PENDING)
                .status(ConsentStatus.DRAFT)
                .build();

        Consent savedConsent = consentRepository.save(consent);

        notificationService.notify(
                artist.getId(),
                "Contract Ready for Review",
                "You have a new contract from " + recruiter.getFullName() + " for " + job.getTitle(),
                "/contracts/" + savedConsent.getId(),
                NotificationType.CONTRACT
        );

        notificationService.notify(
                recruiter.getId(),
                "Contract Created",
                "Contract for " + artist.getFullName() + " has been created successfully",
                "/contracts/" + savedConsent.getId(),
                NotificationType.CONTRACT
        );

        log.info("Contract created: Recruiter {} hired {} for job {}",
                recruiterEmail, artist.getEmail(), job.getTitle());

        return savedConsent;
    }

    @Override
    @Transactional
    public ConsentResponse createConsent(CreateConsentRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found: " + request.getApplicationId()));

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("Consent can only be created for an ACCEPTED application");
        }

        Job job = application.getJob();
        User artist = application.getArtist();
        User recruiter = job.getRecruiter().getUser();

        if (consentRepository.findByJobAndArtist(job, artist).isPresent()) {
            throw new DuplicateResourceException("Consent already exists for this application");
        }

        Consent consent = Consent.builder()
                .job(job)
                .recruiter(recruiter)
                .artist(artist)
                .agreedSalary(job.getSalary() != null ? job.getSalary() : BigDecimal.ZERO)
                .startDate(LocalDateTime.now())
                .terms(request.getTerms())
                .approvalStatus(ApprovalStatus.PENDING)
                .status(ConsentStatus.DRAFT)
                .build();

        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
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

        String recruiterUserId = consent.getRecruiter().getId();
        String artistUserId = consent.getArtist().getId();

        if (!recruiterUserId.equals(user.getId()) && !artistUserId.equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view this contract"
            );
        }

        return consentMapper.toResponse(consent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByRecruiter(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recruiter not found"
                ));

        return consentRepository.findByRecruiter(recruiter).stream()
                .map(consentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByArtist(String email) {
        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Artist not found"
                ));

        return consentRepository.findByArtist(artist).stream()
                .map(consentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getPendingConsents() {
        return consentRepository.findAll().stream()
                .filter(c -> c.getApprovalStatus() == ApprovalStatus.PENDING)
                .map(consentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConsentResponse approveConsent(Long consentId, String managerId) {
        Consent consent = findConsent(String.valueOf(consentId));
        if (consent.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending consents can be approved");
        }

        consent.setApprovalStatus(ApprovalStatus.APPROVED);
        consent.setApprovedBy(managerId);
        consent.setApprovedAt(LocalDateTime.now());
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
    @Transactional
    public ConsentResponse rejectConsent(Long consentId, String managerId) {
        Consent consent = findConsent(String.valueOf(consentId));
        if (consent.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending consents can be rejected");
        }

        consent.setApprovalStatus(ApprovalStatus.REJECTED);
        consent.setApprovedBy(managerId);
        consent.setApprovedAt(LocalDateTime.now());
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
    @Transactional
    public ConsentResponse archiveConsent(Long consentId) {
        Consent consent = findConsent(String.valueOf(consentId));
        if (consent.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Only approved consents can be archived");
        }

        consent.setStatus(ConsentStatus.COMPLETED);
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    private Consent findConsent(String consentId) {
        return consentRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found: " + consentId));
    }
}
