package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.dto.CreateConsentRequest;

import java.util.List;

public interface ConsentService {

    ConsentResponse createConsent(CreateConsentRequest request);

    List<ConsentResponse> getPendingConsents();

    ConsentResponse approveConsent(Long consentId, String managerId);

    ConsentResponse rejectConsent(Long consentId, String managerId);

    ConsentResponse archiveConsent(Long consentId);
}
