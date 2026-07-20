package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CreateSkillRequest;
import com.huza.huzabackend.entity.Category;
import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.CategoryRepository;
import com.huza.huzabackend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Skill> getAllOrSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return skillRepository.findAll();
        }
        return skillRepository.searchSkills(search.trim());
    }

    @Transactional
    public Skill createCatalogSkill(CreateSkillRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty");
        }

        String trimmedName = request.getName().trim();

        if (skillRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new IllegalArgumentException("Skill already exists: " + trimmedName);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Skill skill = new Skill(null, trimmedName, category);
        return skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + id));
    }

    @Transactional
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found with ID: " + id);
        }
        skillRepository.deleteById(id);
    }
}