package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ConsentMapper;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.dto.CreateConsentRequest;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.Consent;
import com.huza.huzabackend.entity.ConsentApprovalStatus;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.ConsentRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ConsentMapper consentMapper;

    @Override
    @Transactional
    public ConsentResponse createConsent(CreateConsentRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found: " + request.getApplicationId()));

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("Consent can only be created for an ACCEPTED application");
        }

        if (consentRepository.existsByApplication_Id(application.getId())) {
            throw new DuplicateResourceException("Consent already exists for this application");
        }

        User manager = null;
        if (request.getManagerId() != null && !request.getManagerId().isBlank()) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found: " + request.getManagerId()));
        }

        Consent consent = Consent.builder()
                .application(application)
                .manager(manager)
                .paymentDuration(request.getPaymentDuration())
                .terms(request.getTerms())
                .approvalStatus(ConsentApprovalStatus.PENDING)
                .build();

        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getPendingConsents() {
        return consentRepository.findAllByApprovalStatusWithDetails(ConsentApprovalStatus.PENDING).stream()
                .map(consentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConsentResponse approveConsent(Long consentId, String managerId) {
        Consent consent = findConsent(consentId);
        if (consent.getApprovalStatus() != ConsentApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending consents can be approved");
        }

        if (managerId != null && !managerId.isBlank()) {
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerId));
            consent.setManager(manager);
        }

        consent.setApprovalStatus(ConsentApprovalStatus.APPROVED);
        consent.setApprovedAt(LocalDateTime.now());
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
    @Transactional
    public ConsentResponse rejectConsent(Long consentId, String managerId) {
        Consent consent = findConsent(consentId);
        if (consent.getApprovalStatus() != ConsentApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending consents can be rejected");
        }

        if (managerId != null && !managerId.isBlank()) {
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerId));
            consent.setManager(manager);
        }

        consent.setApprovalStatus(ConsentApprovalStatus.REJECTED);
        consent.setApprovedAt(LocalDateTime.now());
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    @Override
    @Transactional
    public ConsentResponse archiveConsent(Long consentId) {
        Consent consent = findConsent(consentId);
        if (consent.getApprovalStatus() != ConsentApprovalStatus.APPROVED) {
            throw new IllegalStateException("Only approved consents can be archived");
        }

        consent.setApprovalStatus(ConsentApprovalStatus.ARCHIVED);
        return consentMapper.toResponse(consentRepository.save(consent));
    }

    private Consent findConsent(Long consentId) {
        return consentRepository.findByIdWithDetails(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found: " + consentId));
    }
}
