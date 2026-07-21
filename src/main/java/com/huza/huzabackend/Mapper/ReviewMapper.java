package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "job.jobId", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle")
    @Mapping(source = "recruiter.user.id", target = "recruiterUserId")
    @Mapping(source = "recruiter.user.fullName", target = "recruiterName")
    @Mapping(source = "artist.id", target = "artistId")
    @Mapping(source = "artist.user.fullName", target = "artistName")
    ReviewResponse toResponse(Review review);
}
