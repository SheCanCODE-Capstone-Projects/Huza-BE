package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.CreateSkillRequest;
import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // GET /api/skills            -> all skills
    // GET /api/skills?search=xyz -> filtered by name
    @GetMapping
    public ResponseEntity<List<Skill>> getAllOrSearch(
            @RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(skillService.getAllOrSearch(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Skill> addCatalogSkill(@Valid @RequestBody CreateSkillRequest request) {
        Skill created = skillService.createCatalogSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}