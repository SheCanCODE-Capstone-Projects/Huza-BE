package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ConsentResponse;
import com.huza.huzabackend.entity.Consent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsentMapper {

    @Mapping(source = "job.jobId", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle")
    @Mapping(source = "recruiter.id", target = "recruiterId")
    @Mapping(source = "recruiter.fullName", target = "recruiterName")
    @Mapping(source = "artist.id", target = "artistId")
    @Mapping(source = "artist.fullName", target = "artistName")
    @Mapping(source = "artist.email", target = "artistEmail")
    @Mapping(source = "approvedBy", target = "adminId")
    ConsentResponse toResponse(Consent consent);
}
