package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {
    
    @NotBlank(message = "Job ID is required")
    private String jobId;
    
    private String coverLetter;
    
    private String resumeUrl;
}
