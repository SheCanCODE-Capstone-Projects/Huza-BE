package com.huza.huzabackend.dto;

import lombok.Data;

@Data
public class ArtistProfileUpdateRequest {
    private String bio;
    private String headline;
    private Integer experienceYears;
    private String education;
    private String location;
    private String socialLinks;
}