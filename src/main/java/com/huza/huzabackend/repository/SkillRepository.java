package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Skill> searchSkills(@Param("search") String search);

    // NEW — true only if at least one skill belongs to this category
    boolean existsByCategory_CategoryId(Long categoryId);
}