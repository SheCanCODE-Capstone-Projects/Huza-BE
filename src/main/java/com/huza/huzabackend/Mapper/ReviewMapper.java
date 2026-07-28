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
    @Mapping(source = "reviewer.email", target = "reviewerEmail")
    @Mapping(source = "reviewedUser.id", target = "reviewedUserId")
    @Mapping(source = "reviewedUser.fullName", target = "reviewedUserName")
    @Mapping(source = "reviewedUser.email", target = "reviewedUserEmail")
    ReviewResponse toResponse(Review review);
}

