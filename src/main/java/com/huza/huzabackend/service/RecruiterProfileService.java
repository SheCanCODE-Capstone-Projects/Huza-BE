package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;

public interface RecruiterProfileService {
    RecruiterProfileResponse getRecruiterProfileByUserId(String userId);
    RecruiterProfileResponse updateRecruiterProfileByUserId(String userId, UpdateRecruiterProfileRequest request);
}