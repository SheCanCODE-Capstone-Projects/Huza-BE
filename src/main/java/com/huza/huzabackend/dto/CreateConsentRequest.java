package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateConsentRequest {

    @NotBlank
    private String applicationId;

    private String adminId;

    private String paymentDuration;

    private String terms;
}
