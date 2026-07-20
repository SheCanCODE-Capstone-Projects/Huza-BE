package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.WorkExperienceRequest;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recruiter/profile")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<RecruiterProfileResponse> getProfile(@PathVariable String userId) {
        RecruiterProfileResponse response = recruiterProfileService.getRecruiterProfileByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RecruiterProfileResponse> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateRecruiterProfileRequest request) {
        RecruiterProfileResponse response = recruiterProfileService.updateRecruiterProfileByUserId(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{recruiterId}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecruiterProfileResponse> uploadProfilePicture(
            @PathVariable String recruiterId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(recruiterProfileService.uploadProfilePicture(recruiterId, file));
    }

    @DeleteMapping("/{recruiterId}/picture")
    public ResponseEntity<RecruiterProfileResponse> removeProfilePicture(@PathVariable String recruiterId) {
        return ResponseEntity.ok(recruiterProfileService.removeProfilePicture(recruiterId));
    }

    @PutMapping("/{recruiterId}/work")
    public ResponseEntity<RecruiterProfileResponse> updateWorkExperience(
            @PathVariable String recruiterId,
            @Valid @RequestBody WorkExperienceRequest request) {
        return ResponseEntity.ok(recruiterProfileService.updateWorkExperience(recruiterId, request));
    }
}