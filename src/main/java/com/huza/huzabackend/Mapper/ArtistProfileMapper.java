package com.huza.huzabackend.Mapper;

import com.huza.huzabackend.dto.ArtistProfileResponseDTO;
import com.huza.huzabackend.dto.ArtistProfileUpdateRequest;
import com.huza.huzabackend.entity.ArtistProfile;
import com.huza.huzabackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArtistProfileMapper {

    @Mapping(source = "user.id", target = "artistId")
    @Mapping(source = "user.fullName", target = "name")   // was user.username
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "profile.bio", target = "bio")
    @Mapping(source = "profile.headline", target = "headline")
    @Mapping(source = "profile.experienceYears", target = "experienceYears")
    @Mapping(source = "profile.education", target = "education")
    @Mapping(source = "profile.location", target = "location")
    @Mapping(source = "profile.socialLinks", target = "socialLinks")
    ArtistProfileResponseDTO toResponseDto(User user, ArtistProfile profile);

    void updateProfileFromDto(ArtistProfileUpdateRequest dto, @MappingTarget ArtistProfile profile);
}