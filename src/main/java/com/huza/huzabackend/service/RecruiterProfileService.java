package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.dto.WorkExperienceRequest;
import org.springframework.web.multipart.MultipartFile;

public interface RecruiterProfileService {
    RecruiterProfileResponse getRecruiterProfileByUserId(String userId);
    RecruiterProfileResponse updateRecruiterProfileByUserId(String userId, UpdateRecruiterProfileRequest request);
    RecruiterProfileResponse uploadProfilePicture(String userId, MultipartFile file);
    RecruiterProfileResponse removeProfilePicture(String userId);
    RecruiterProfileResponse updateWorkExperience(String userId, WorkExperienceRequest request);
}