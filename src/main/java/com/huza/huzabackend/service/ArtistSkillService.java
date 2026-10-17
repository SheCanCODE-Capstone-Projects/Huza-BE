package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ArtistSkillResponseDTO;
import com.huza.huzabackend.entity.ArtistProfile;
import com.huza.huzabackend.entity.ArtistSkill;
import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.repository.ArtistProfileRepository;
import com.huza.huzabackend.repository.ArtistSkillRepository;
import com.huza.huzabackend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistSkillService {

    private final ArtistSkillRepository artistSkillRepository;
    private final SkillRepository skillRepository;
    private final ArtistProfileRepository artistProfileRepository;

    @Transactional(readOnly = true)
    public List<ArtistSkillResponseDTO> getSkillsByArtist(String artistId) {
        return artistSkillRepository.findByArtistProfileId(artistId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ArtistSkillResponseDTO assignSkillToArtist(String artistId, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill ID " + skillId + " not found in global catalog. Create it first!"));

        ArtistProfile artist = artistProfileRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("Artist with ID " + artistId + " does not exist."));

        ArtistSkill saved = artistSkillRepository.findByArtistProfileIdAndSkillId(artistId, skillId)
                .orElseGet(() -> artistSkillRepository.save(new ArtistSkill(artist, skill)));

        return toDto(saved);
    }

    @Transactional
    public void removeSkillFromArtist(Long artistSkillId) {
        if (!artistSkillRepository.existsById(artistSkillId)) {
            throw new IllegalArgumentException("Skill assignment ID not found.");
        }
        artistSkillRepository.deleteById(artistSkillId);
    }

    private ArtistSkillResponseDTO toDto(ArtistSkill artistSkill) {
        return new ArtistSkillResponseDTO(
                artistSkill.getId(),
                artistSkill.getArtistProfile().getId(),
                artistSkill.getSkill().getId(),
                artistSkill.getSkill().getName()
        );
    }
}