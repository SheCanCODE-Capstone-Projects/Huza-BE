package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.PortfolioRequest;
import com.huza.huzabackend.dto.PortfolioResponse;
import com.huza.huzabackend.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/portfolio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Portfolio", description = "Portfolio management for artists")
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * Create portfolio item
     * POST /api/artist/portfolio
     */
    @PostMapping
    @Operation(summary = "Create portfolio item")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            @RequestParam String artistId,
            @Valid @RequestBody PortfolioRequest request) {

        log.info("📁 Creating portfolio for artist: {}", artistId);

        try {
            PortfolioResponse response = portfolioService.createPortfolio(artistId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Portfolio created successfully", response));

        } catch (Exception e) {
            log.error("❌ Failed to create portfolio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create portfolio: " + e.getMessage()));
        }
    }

    /**
     * Get all portfolio items for an artist
     * GET /api/artist/portfolio/{artistId}
     */
    @GetMapping("/{artistId}")
    @Operation(summary = "Get all portfolio items for an artist")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfolioByArtist(
            @PathVariable String artistId) {

        log.info("📋 Fetching portfolio for artist: {}", artistId);

        try {
            List<PortfolioResponse> responses = portfolioService.getPortfolioByArtist(artistId);
            return ResponseEntity.ok(ApiResponse.success("Portfolio fetched successfully", responses));

        } catch (Exception e) {
            log.error("❌ Failed to fetch portfolio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Failed to fetch portfolio: " + e.getMessage()));
        }
    }

    /**
     * Update portfolio item
     * PUT /api/artist/portfolio/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update portfolio item")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
    public ResponseEntity<ApiResponse<PortfolioResponse>> updatePortfolio(
            @PathVariable String id,
            @RequestParam String artistId,
            @Valid @RequestBody PortfolioRequest request) {

        log.info("✏️ Updating portfolio: {} for artist: {}", id, artistId);

        try {
            PortfolioResponse response = portfolioService.updatePortfolio(id, artistId, request);
            return ResponseEntity.ok(ApiResponse.success("Portfolio updated successfully", response));

        } catch (Exception e) {
            log.error("❌ Failed to update portfolio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update portfolio: " + e.getMessage()));
        }
    }

    /**
     * Delete portfolio item
     * DELETE /api/artist/portfolio/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete portfolio item")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @PathVariable String id,
            @RequestParam String artistId) {

        log.info("🗑️ Deleting portfolio: {} for artist: {}", id, artistId);

        try {
            portfolioService.deletePortfolio(id, artistId);
            return ResponseEntity.ok(ApiResponse.success("Portfolio deleted successfully", null));

        } catch (Exception e) {
            log.error("❌ Failed to delete portfolio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete portfolio: " + e.getMessage()));
        }
    }
}