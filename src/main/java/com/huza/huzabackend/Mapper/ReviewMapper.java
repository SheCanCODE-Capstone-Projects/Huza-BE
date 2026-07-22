package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ReviewResponse;
import com.huza.huzabackend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "reviewer.id", target = "reviewerId")
    @Mapping(source = "reviewer.fullName", target = "reviewerName")
    @Mapping(source = "reviewedUser.id", target = "reviewedUserId")
    @Mapping(source = "reviewedUser.fullName", target = "reviewedUserName")
    @Mapping(source = "consent.consentId", target = "consentId")
    @Mapping(source = "consent.application.job.jobId", target = "jobId")
    @Mapping(source = "consent.application.job.title", target = "jobTitle")
    ReviewResponse toResponse(Review review);
}
