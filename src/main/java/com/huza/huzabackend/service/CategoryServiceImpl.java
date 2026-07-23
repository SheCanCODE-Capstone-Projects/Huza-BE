package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CategoryRequest;
import com.huza.huzabackend.dto.CategoryResponse;
import com.huza.huzabackend.entity.Category;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = request.getCategoryName().trim();
        if (categoryRepository.existsByCategoryNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Category already exists with name: " + name);
        }

        Category category = Category.builder()
                .categoryName(name)
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        String newName = request.getCategoryName().trim();
        if (categoryRepository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(newName, categoryId)) {
            throw new DuplicateResourceException("Category already exists with name: " + newName);
        }

        category.setCategoryName(newName);
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .build();
    }
}