package com.huza.huzabackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"artist_id", "skill_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ArtistSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artistProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public ArtistSkill(ArtistProfile artistProfile, Skill skill) {
        this.artistProfile = artistProfile;
        this.skill = skill;
    }
}