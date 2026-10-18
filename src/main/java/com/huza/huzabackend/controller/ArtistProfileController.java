package com.huza.huzabackend.controller;

import org.springframework.http.MediaType;
import com.huza.huzabackend.dto.ArtistProfileResponseDTO;
import com.huza.huzabackend.dto.ArtistProfileUpdateRequest;
import com.huza.huzabackend.service.ArtistProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/artist/profile")
@RequiredArgsConstructor
public class ArtistProfileController {

    private final ArtistProfileService artistProfileService;

    @GetMapping("/{artistId}")
public ResponseEntity<ArtistProfileResponseDTO> getProfile(@PathVariable String artistId) {
        String cleanedId = artistId.trim();
        ArtistProfileResponseDTO response = artistProfileService.getArtistProfile(cleanedId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<ArtistProfileResponseDTO> updateProfile(
            @PathVariable String artistId,
            @Valid @RequestBody ArtistProfileUpdateRequest request) {
        String cleanedId = artistId.trim();
        ArtistProfileResponseDTO updatedResponse = artistProfileService.updateArtistProfile(cleanedId, request);
        return ResponseEntity.ok(updatedResponse);
    }

    @PostMapping(value = "/{artistId}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistProfileResponseDTO> uploadProfilePicture(
            @PathVariable String artistId,
            @RequestParam("file") MultipartFile file) {

        ArtistProfileResponseDTO updatedProfile = artistProfileService.uploadProfilePicture(artistId.trim(), file);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{artistId}/picture")
    public ResponseEntity<ArtistProfileResponseDTO> deleteProfilePicture(@PathVariable String artistId) {
        ArtistProfileResponseDTO updatedProfile = artistProfileService.deleteProfilePicture(artistId.trim());
        return ResponseEntity.ok(updatedProfile);
    }
}