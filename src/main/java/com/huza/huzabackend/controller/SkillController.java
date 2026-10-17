package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.CreateSkillRequest;
import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // GET /api/skills or /api/skills?search=Guitar
    @GetMapping
    public ResponseEntity<List<Skill>> browseCatalog(@RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(skillService.getAllOrSearch(search));
    }

    // POST /api/skills (To add new base items to catalog)
    @PostMapping
    public ResponseEntity<Skill> addCatalogSkill(@Valid @RequestBody CreateSkillRequest request) {
        Skill created = skillService.createCatalogSkill(request);
        return ResponseEntity.ok(created);
    }
}