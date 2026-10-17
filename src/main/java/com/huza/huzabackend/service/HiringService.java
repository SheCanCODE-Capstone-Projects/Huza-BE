package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ConsentRequest;
import com.huza.huzabackend.entity.Consent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HiringService {

    private final ConsentService consentService;

    @Transactional
    public Consent hireArtist(String recruiterEmail, ConsentRequest request) {
        log.info("Hiring artist: Recruiter {} is hiring with application {}",
                recruiterEmail, request.getApplicationId());

        // Delegate to ConsentService which handles all the validation
        return consentService.createConsent(recruiterEmail, request);
    }
}