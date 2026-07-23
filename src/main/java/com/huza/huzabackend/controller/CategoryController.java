package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.CategoryRequest;
import com.huza.huzabackend.dto.CategoryResponse;
import com.huza.huzabackend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Job Categories", description = "Endpoints for managing job categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new job category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CategoryResponse>builder()
                        .success(true)
                        .message("Category created successfully")
                        .data(category)
                        .build());
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Fetch category details by category ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category retrieved successfully")
                .data(category)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve list of all job categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .message("Categories retrieved successfully")
                .data(categories)
                .build());
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update category name and description by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category updated successfully")
                .data(category)
                .build());
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Remove a job category by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted successfully")
                .build());
    }
}