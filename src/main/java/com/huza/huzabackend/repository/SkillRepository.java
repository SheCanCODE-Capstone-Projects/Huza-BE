package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Case-insensitive search for browsing the catalog
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Skill> searchSkills(@Param("search") String search);
}