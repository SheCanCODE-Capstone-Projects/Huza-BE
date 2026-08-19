package com.huza.huzabackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotBlank(message = "Reviewed user id is required")
    private String reviewedUserId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;

    private String consentId;
}