package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}