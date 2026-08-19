package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // Read-only for artist side
}
