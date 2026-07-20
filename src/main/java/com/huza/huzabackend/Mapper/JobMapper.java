package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {

    @Mapping(source = "postedBy", target = "recruiterUserId")
    @Mapping(target = "recruiterName", ignore = true)
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    JobResponse toResponse(Job job);
}