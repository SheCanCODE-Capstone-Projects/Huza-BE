package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.dto.WorkExperienceRequest;
import com.huza.huzabackend.entity.RecruiterProfile;
import com.huza.huzabackend.entity.Role;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.Mapper.RecruiterProfileMapper;
import com.huza.huzabackend.repository.RecruiterProfileRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileMapper recruiterProfileMapper;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB restriction
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");

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

    @Override
    @Transactional
    public RecruiterProfileResponse uploadProfilePicture(String userId, MultipartFile file) {
        // Find using the String User ID instead of strict entity primary key Long ID
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        // 1. Validation: Check empty payload
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        // 2. Validation: Strict File Size Bound Check
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum threshold of 2MB");
        }

        // 3. Validation: File Format whitelist constraint
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, JPG, and PNG are allowed.");
        }

        try {
            User user = profile.getUser();
            if (user != null) {
                // Storing metadata directly back to our asset targets
                user.setProfilePicture(file.getOriginalFilename());
                user.setProfilePictureContentType(file.getContentType());
                user.setProfilePictureData(file.getBytes());
                userRepository.save(user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process and store file bytes safely", e);
        }

        return recruiterProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileResponse removeProfilePicture(String userId) {
        // Find using the String User ID instead of strict entity primary key Long ID
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + userId));

        User user = profile.getUser();
        if (user != null) {
            // Wipe asset references out completely
            user.setProfilePicture(null);
            user.setProfilePictureContentType(null);
            user.setProfilePictureData(null);
            userRepository.save(user);
        }

        return recruiterProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileResponse updateWorkExperience(String userId, WorkExperienceRequest request) {
        // Find using the String User ID instead of strict entity primary key Long ID
        RecruiterProfile profile = recruiterProfileRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        // Update background content fields safely (supports empty/blank configurations)
        profile.setJobTitle(request.getExperience() == null ? "" : request.getExperience());
        RecruiterProfile updatedProfile = recruiterProfileRepository.save(profile);

        return recruiterProfileMapper.toResponse(updatedProfile);
    }
}