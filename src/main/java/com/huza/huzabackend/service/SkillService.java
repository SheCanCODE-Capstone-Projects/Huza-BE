package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CreateSkillRequest;
import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> getAllOrSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return skillRepository.findAll();
        }
        return skillRepository.searchSkills(search.trim());
    }

    public Skill createCatalogSkill(CreateSkillRequest request) {
        Skill skill = new Skill(null, request.getName()); // id explicitly null → forces INSERT
        return skillRepository.save(skill);
    }
}