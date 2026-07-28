package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ArtistSkillResponseDTO;
import com.huza.huzabackend.dto.AssignSkillRequest;
import com.huza.huzabackend.service.ArtistSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArtistSkillController {

    private final ArtistSkillService artistSkillService;

    // GET /api/artist/profile/{artistId}/skills
    @GetMapping("/artist/profile/{artistId}/skills")
    public ResponseEntity<List<ArtistSkillResponseDTO>> getArtistSkills(@PathVariable String artistId) {
        return ResponseEntity.ok(artistSkillService.getSkillsByArtist(artistId));
    }

    // POST /api/artist/profile/{artistId}/skills
    @PostMapping("/artist/profile/{artistId}/skills")
    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    public ResponseEntity<?> assignSkill(
            @PathVariable String artistId,
            @Valid @RequestBody AssignSkillRequest request) {
        try {
            ArtistSkillResponseDTO created = artistSkillService.assignSkillToArtist(artistId, request.getSkillId());
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/artist/skills/{artistSkillId}
    @DeleteMapping("/artist/skills/{artistSkillId}")
    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    public ResponseEntity<String> removeSkill(@PathVariable Long artistSkillId) {
        try {
            artistSkillService.removeSkillFromArtist(artistSkillId);
            return ResponseEntity.ok("Skill removed from artist profile.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}