package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Artist ID is required")
    private String artistId;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private String coverLetter;

    private String resumeUrl;
}
