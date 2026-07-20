package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRecruiterProfileRequest {
    private String bio;
    private String location;
    @NotBlank(message = "Job title is required")
    private String jobTitle;
}