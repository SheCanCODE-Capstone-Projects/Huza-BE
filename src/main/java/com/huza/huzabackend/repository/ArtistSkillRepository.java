package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.ArtistSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ArtistSkillRepository extends JpaRepository<ArtistSkill, Long> {
    List<ArtistSkill> findByArtistProfileId(String artistId);
    Optional<ArtistSkill> findByArtistProfileIdAndSkillId(String artistId, Long skillId);
}