package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.entity.RecruiterProfile;
import com.huza.huzabackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RecruiterProfileMapper {

    public RecruiterProfileResponse toResponse(RecruiterProfile profile) {
        if (profile == null) return null;

        RecruiterProfileResponse response = new RecruiterProfileResponse();
        response.setRecruiterId(profile.getRecruiterId());
        response.setJobTitle(profile.getJobTitle());

        User user = profile.getUser();
        if (user != null) {
            response.setUserId(user.getId());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setBio(user.getBio());
            response.setLocation(user.getLocation());
            if (user.getRecruiterType() != null) {
                response.setRecruiterType(user.getRecruiterType().name());
            }
        }

        // Company mapping removed temporary until Company entity is added to the project
        return response;
    }
}