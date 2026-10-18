package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Portfolio response")
public class PortfolioResponse {

    @Schema(description = "Portfolio ID")
    private String id;

    @Schema(description = "Portfolio title")
    private String title;

    @Schema(description = "Portfolio description")
    private String description;

    @Schema(description = "File URL")
    private String fileUrl;

    @Schema(description = "File type")
    private FileType fileType;

    @Schema(description = "Is featured")
    private boolean isFeatured;

    @Schema(description = "Artist ID")
    private String artistId;

    @Schema(description = "Artist name")
    private String artistName;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}