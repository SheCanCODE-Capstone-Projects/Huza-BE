package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for creating/updating portfolio")
public class PortfolioRequest {

    @Schema(description = "Portfolio title", example = "My Photography Project")
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Portfolio description", example = "A collection of my best photography work")
    private String description;

    @Schema(description = "File URL", example = "https://storage.com/my-image.jpg")
    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @Schema(description = "File type", example = "IMAGE", allowableValues = {"IMAGE", "VIDEO", "PDF"})
    @NotNull(message = "File type is required")
    private FileType fileType;

    @Schema(description = "Set as featured", example = "false")
    private boolean isFeatured = false;
}