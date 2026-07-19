package com.huza.huzabackend.service.impl;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.entity.RecruiterProfile;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.Mapper.RecruiterProfileMapper;
import com.huza.huzabackend.repository.RecruiterProfileRepository;
import com.huza.huzabackend.repository.UserRepository;
import com.huza.huzabackend.service.RecruiterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileMapper recruiterProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponse getRecruiterProfileByUserId(String userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + userId));
        return recruiterProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileResponse updateRecruiterProfileByUserId(String userId, UpdateRecruiterProfileRequest request) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + userId));

        profile.setJobTitle(request.getJobTitle());

        User user = profile.getUser();
        if (user != null) {
            user.setBio(request.getBio());
            user.setLocation(request.getLocation());
            userRepository.save(user);
        }

        RecruiterProfile updatedProfile = recruiterProfileRepository.save(profile);
        return recruiterProfileMapper.toResponse(updatedProfile);
    }
}