package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ConsentRequest;
import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.dto.CreateConsentRequest;
import com.huza.huzabackend.entity.Consent;

import java.util.List;

public interface ConsentService {
    Consent createConsent(String recruiterEmail, ConsentRequest request);
    ConsentResponse createConsent(CreateConsentRequest request);
    ConsentResponse getConsentById(String id, String email);
    List<ConsentResponse> getConsentsByRecruiter(String email);
    List<ConsentResponse> getConsentsByArtist(String email);
    List<ConsentResponse> getPendingConsents();
    ConsentResponse approveConsent(Long consentId, String managerId);
    ConsentResponse rejectConsent(Long consentId, String managerId);
    ConsentResponse archiveConsent(Long consentId);
}