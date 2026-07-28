package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CategoryRequest;
import com.huza.huzabackend.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long categoryId);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);
    void deleteCategory(Long categoryId);
}