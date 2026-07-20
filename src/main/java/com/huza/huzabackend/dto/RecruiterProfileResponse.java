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
    private String recruiterType;
    private String jobTitle;

    // Add these fields to match the MapStruct target configuration
    private String profilePicture;
    private String profilePictureContentType;
    private byte[] profilePictureData;
}