package com.huza.huzabackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
// Import the User entity if it lives in a different package
// For example, if it's in the same entity folder:
// import com.huza.huzabackend.entity.User;

@JsonIgnoreProperties("user")
@Entity
@Data
public class ArtistProfile {

    @Id
    @Column(name = "artist_id") // Shared primary key
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "artist_id")
    private User user;

    private String bio;
    private String headline;
    private Integer experienceYears;
    private String education;
    private String location;
    private String socialLinks;
}

