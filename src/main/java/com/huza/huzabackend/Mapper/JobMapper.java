package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {

    @Mapping(source = "recruiter.user.id", target = "recruiterUserId")
    @Mapping(source = "recruiter.user.fullName", target = "recruiterName")
    @Mapping(source = "company.companyId", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    JobResponse toResponse(Job job);
}