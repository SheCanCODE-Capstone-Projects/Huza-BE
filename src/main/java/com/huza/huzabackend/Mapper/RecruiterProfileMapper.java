package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.RecruiterProfileResponse;
import com.huza.huzabackend.entity.RecruiterProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecruiterProfileMapper {

    @Mapping(source = "recruiterId", target = "recruiterId")
    @Mapping(source = "jobTitle", target = "jobTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.bio", target = "bio")
    @Mapping(source = "user.location", target = "location")
    @Mapping(source = "user.recruiterType", target = "recruiterType")
    // Only map the filename string since the DTO doesn't have the byte[] fields
    @Mapping(source = "user.profilePicture", target = "profilePicture")
    RecruiterProfileResponse toResponse(RecruiterProfile profile);
}