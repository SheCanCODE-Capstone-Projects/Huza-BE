package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.dto.UpdateRecruiterProfileRequest;
import com.huza.huzabackend.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter/profile")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    // Now accepts the UUID string format securely
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
}