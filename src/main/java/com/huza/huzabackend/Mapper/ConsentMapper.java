package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.entity.Consent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsentMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "application.job.jobId", target = "jobId")
    @Mapping(source = "application.job.title", target = "jobTitle")
    @Mapping(source = "application.artist.id", target = "artistId")
    @Mapping(source = "application.artist.fullName", target = "artistName")
    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.fullName", target = "managerName")
    ConsentResponse toResponse(Consent consent);
}
