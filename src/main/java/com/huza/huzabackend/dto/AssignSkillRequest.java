package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignSkillRequest {

    @NotNull(message = "skillId is required")
    private Long skillId;
}