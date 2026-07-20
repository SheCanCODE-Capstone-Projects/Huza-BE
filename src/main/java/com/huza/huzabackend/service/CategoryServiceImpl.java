package com.huza.huzabackend.service.impl;

import com.huza.huzabackend.dto.CategoryResponse;
import com.huza.huzabackend.repository.CategoryRepository;
import com.huza.huzabackend.service.CategoryService;
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
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryResponse.builder()
                        .categoryId(c.getCategoryId())
                        .categoryName(c.getCategoryName())
                        .description(c.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}