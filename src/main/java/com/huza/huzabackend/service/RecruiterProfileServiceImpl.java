package com.huza.huzabackend.service.impl;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.entity.RecruiterProfile;
import com.huza.huzabackend.entity.Role;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.Mapper.RecruiterProfileMapper;
import com.huza.huzabackend.repository.RecruiterProfileRepository;
import com.huza.huzabackend.repository.UserRepository;
import com.huza.huzabackend.service.RecruiterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileMapper recruiterProfileMapper;

    @Override
    @Transactional
    public RecruiterProfileResponse getRecruiterProfileByUserId(String userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        return recruiterProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileResponse updateRecruiterProfileByUserId(String userId, UpdateRecruiterProfileRequest request) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> createDefaultProfile(userId));

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

    // Lazily creates the RecruiterProfile row the first time it's needed,
    // instead of 404ing for recruiters who registered before a profile existed.
    private RecruiterProfile createDefaultProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getRole() != Role.RECRUITER) {
            throw new ResourceNotFoundException("Recruiter profile not found for user ID: " + userId);
        }

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUser(user);
        profile.setJobTitle("Recruiter");
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        return recruiterProfileRepository.save(profile);
    }
}