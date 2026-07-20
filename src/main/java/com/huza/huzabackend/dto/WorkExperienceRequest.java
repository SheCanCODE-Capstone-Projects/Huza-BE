package com.huza.huzabackend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkExperienceRequest {
    @Size(max = 2000, message = "Experience content cannot exceed 2000 characters")
    private String experience; // recruiter may leave this blank
}