package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConsentRequest {

    @NotBlank
    private String applicationId;

    private String managerId;

    private String paymentDuration;

    private String terms;
}
