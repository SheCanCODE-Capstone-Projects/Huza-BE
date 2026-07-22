package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "jobId", source = "job.jobId")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "jobStatus", source = "job.status")
    @Mapping(target = "artistId", source = "artist.id")
    @Mapping(target = "artistName", source = "artist.fullName")
    ApplicationResponse toResponse(Application application);
}