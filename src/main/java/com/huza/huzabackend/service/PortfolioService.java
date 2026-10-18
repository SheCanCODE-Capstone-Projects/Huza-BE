package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.PortfolioRequest;
import com.huza.huzabackend.dto.PortfolioResponse;
import com.huza.huzabackend.entity.Portfolio;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.entity.FileType;
import com.huza.huzabackend.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    /**
     * Create a new portfolio item
     */
    @Transactional
    public PortfolioResponse createPortfolio(String artistId, PortfolioRequest request) {
        log.info("📁 Creating portfolio for artist: {}", artistId);

        User artist = userService.findById(artistId);

        // Validate file type
        if (request.getFileType() == null) {
            throw new IllegalArgumentException("File type is required");
        }

        Portfolio portfolio = Portfolio.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType())
                .isFeatured(request.isFeatured())
                .artist(artist)
                .build();

        // If this portfolio is set as featured, unset other featured items
        if (portfolio.isFeatured()) {
            unsetOtherFeatured(artistId);
        }

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("✅ Portfolio created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Get all portfolio items for an artist
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolioByArtist(String artistId) {
        log.info("📋 Fetching portfolio for artist: {}", artistId);

        User artist = userService.findById(artistId);
        List<Portfolio> portfolios = portfolioRepository.findByArtist(artist);

        return portfolios.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update a portfolio item
     */
    @Transactional
    public PortfolioResponse updatePortfolio(String portfolioId, String artistId, PortfolioRequest request) {
        log.info("✏️ Updating portfolio: {} for artist: {}", portfolioId, artistId);

        Portfolio portfolio = portfolioRepository.findByIdAndArtistId(portfolioId, artistId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found or access denied"));

        portfolio.setTitle(request.getTitle());
        portfolio.setDescription(request.getDescription());
        portfolio.setFileUrl(request.getFileUrl());
        portfolio.setFileType(request.getFileType());

        // Handle featured toggle
        boolean wasFeatured = portfolio.isFeatured();
        boolean isFeatured = request.isFeatured();

        if (isFeatured && !wasFeatured) {
            unsetOtherFeatured(artistId);
        }
        portfolio.setFeatured(isFeatured);

        Portfolio updated = portfolioRepository.save(portfolio);
        log.info("✅ Portfolio updated: {}", updated.getId());

        return mapToResponse(updated);
    }

    /**
     * Delete a portfolio item
     */
    @Transactional
    public void deletePortfolio(String portfolioId, String artistId) {
        log.info("🗑️ Deleting portfolio: {} for artist: {}", portfolioId, artistId);

        Portfolio portfolio = portfolioRepository.findByIdAndArtistId(portfolioId, artistId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found or access denied"));

        portfolioRepository.delete(portfolio);
        log.info("✅ Portfolio deleted: {}", portfolioId);
    }

    /**
     * Unset featured status for all other portfolio items of an artist
     */
    private void unsetOtherFeatured(String artistId) {
        User artist = userService.findById(artistId);
        List<Portfolio> featuredItems = portfolioRepository.findByArtistIdAndIsFeaturedTrue(artistId);
        featuredItems.forEach(item -> {
            item.setFeatured(false);
            portfolioRepository.save(item);
        });
    }

    /**
     * Map Portfolio entity to PortfolioResponse DTO
     */
    private PortfolioResponse mapToResponse(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .fileUrl(portfolio.getFileUrl())
                .fileType(portfolio.getFileType())
                .isFeatured(portfolio.isFeatured())
                .artistId(portfolio.getArtist().getId())
                .artistName(portfolio.getArtist().getFullName())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}