package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameIgnoreCase(String name);

    // Fetch all skills along with their categories in a single query
    @Override
    @Query("SELECT s FROM Skill s JOIN FETCH s.category")
    List<Skill> findAll();

    // Fetch single skill by ID with category
    @Override
    @Query("SELECT s FROM Skill s JOIN FETCH s.category WHERE s.id = :id")
    Optional<Skill> findById(@Param("id") Long id);

    // Search skills while eager-loading category
    @Query("SELECT s FROM Skill s JOIN FETCH s.category WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Skill> searchSkills(@Param("search") String search);

    // Check if any skill belongs to a given category
    boolean existsByCategory_CategoryId(Long categoryId);
}