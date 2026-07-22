package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AssignApplicantsRequest {

    @NotBlank
    private String recruiterUserId;

    @NotEmpty
    @Size(min = 1, max = 2)
    private List<String> selectedApplicationIds;
}
