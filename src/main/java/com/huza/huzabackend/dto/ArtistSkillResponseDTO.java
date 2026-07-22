package com.huza.huzabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistSkillResponseDTO {
    private Long id;
    private String artistId;
    private Long skillId;
    private String skillName;
}