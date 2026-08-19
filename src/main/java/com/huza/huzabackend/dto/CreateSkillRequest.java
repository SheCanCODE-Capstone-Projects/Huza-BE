package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSkillRequest {

    @NotBlank(message = "name is required")
    private String name;
}