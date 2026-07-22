package com.huza.huzabackend.service;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.huza.huzabackend.Mapper.ArtistProfileMapper;
import com.huza.huzabackend.dto.ArtistProfileResponseDTO;
import com.huza.huzabackend.dto.ArtistProfileUpdateRequest;
import com.huza.huzabackend.entity.ArtistProfile;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.ArtistProfileRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistProfileService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final ArtistProfileMapper artistProfileMapper;
    private static final long MAX_PICTURE_SIZE = 5 * 1024 * 1024;
    private String uploadDir = "uploads/profile-pictures";

    @Transactional(readOnly = true)
    public ArtistProfileResponseDTO getArtistProfile(String artistId) {
        // Trim the space but keep it as a String type to match UserRepository
        String cleanedId = artistId.trim();

        User user = userRepository.findById(cleanedId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + cleanedId));

        ArtistProfile profile = user.getArtistProfile();
        if (profile == null) {
            profile = new ArtistProfile();
        }

        return artistProfileMapper.toResponseDto(user, profile);
    }

    @Transactional
    public ArtistProfileResponseDTO updateArtistProfile(String artistId, ArtistProfileUpdateRequest request) {
        // Trim the space but keep it as a String type to match UserRepository
        String cleanedId = artistId.trim();

        User user = userRepository.findById(cleanedId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + cleanedId));

        ArtistProfile profile = user.getArtistProfile();
        if (profile == null) {
            profile = new ArtistProfile();
            profile.setUser(user);
            user.setArtistProfile(profile);
        }

        artistProfileMapper.updateProfileFromDto(request, profile);

        ArtistProfile savedProfile = artistProfileRepository.save(profile);

        return artistProfileMapper.toResponseDto(user, savedProfile);
    }



    @Transactional
    public ArtistProfileResponseDTO uploadProfilePicture(String artistId, MultipartFile file) {
        User user = userRepository.findById(artistId.trim())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + artistId));

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }
        if (file.getSize() > MAX_PICTURE_SIZE) {
            throw new RuntimeException("File too large. Max size is 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        try {
            user.setProfilePictureData(file.getBytes());
            user.setProfilePictureContentType(contentType);
            userRepository.save(user);

            ArtistProfile profile = user.getArtistProfile();
            if (profile == null) {
                profile = new ArtistProfile();
            }

            return artistProfileMapper.toResponseDto(user, profile);

        } catch (IOException e) {
            throw new RuntimeException("Could not read file. Error: " + e.getMessage());
        }
    }

    @Transactional
    public ArtistProfileResponseDTO deleteProfilePicture(String artistId) {
        User user = userRepository.findById(artistId.trim())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + artistId));

        user.setProfilePictureData(null);
        user.setProfilePictureContentType(null);
        userRepository.save(user);

        ArtistProfile profile = user.getArtistProfile();
        if (profile == null) {
            profile = new ArtistProfile();
        }

        return artistProfileMapper.toResponseDto(user, profile);
    }

    @Transactional(readOnly = true)
    public byte[] getProfilePictureBytes(String artistId) {
        User user = userRepository.findById(artistId.trim())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + artistId));

        if (user.getProfilePictureData() == null) {
            throw new RuntimeException("No profile picture found for user: " + artistId);
        }
        return user.getProfilePictureData();
    }

    @Transactional(readOnly = true)
    public String getProfilePictureContentType(String artistId) {
        User user = userRepository.findById(artistId.trim())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + artistId));
        return user.getProfilePictureContentType();
    }
}