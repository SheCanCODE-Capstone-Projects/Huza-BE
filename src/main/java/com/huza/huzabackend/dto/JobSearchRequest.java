package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.ContractType;
import com.huza.huzabackend.entity.ExperienceLevel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchRequest {
    private String category;
    private String location;
    private BigDecimal salary;
    private ExperienceLevel experienceLevel;
    private ContractType contractType;
}
