package com.huza.huzabackend.dto;

import lombok.Data;

@Data
public class ArtistProfileResponseDTO {
    // From User
    private String artistId;
    private String name;
    private String email;

    // From ArtistProfile
    private String bio;
    private String headline;
    private Integer experienceYears;
    private String education;
    private String location;
    private String socialLinks;
}