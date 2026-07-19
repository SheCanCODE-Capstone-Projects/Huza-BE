package com.huza.huzabackend.dto;

import lombok.Data;

@Data
public class RecruiterProfileResponse {
    private Long recruiterId;
    private String userId;
    private String fullName;
    private String email;
    private String username;
    private String phoneNumber;
    private String bio;
    private String location;
    private String jobTitle;
    private String recruiterType;

    // private CompanyInfo company; // Commented out for now
}