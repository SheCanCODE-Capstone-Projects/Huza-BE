package com.huza.huzabackend.config;

import com.huza.huzabackend.entity.Skill;
import com.huza.huzabackend.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class SkillCatalogSeeder {

    @Bean
    CommandLineRunner seedCatalog(SkillRepository skillRepository) {
        return args -> {
            if (skillRepository.count() == 0) {
                skillRepository.saveAll(Arrays.asList(
                        new Skill(null, "Guitar - Beginner"),
                        new Skill(null, "Guitar - Intermediate"),
                        new Skill(null, "Guitar - Expert"),
                        new Skill(null, "Vocalist - Intermediate"),
                        new Skill(null, "Vocalist - Expert"),
                        new Skill(null, "Music Production - Expert"),
                        new Skill(null, "Drums - Intermediate")
                ));
                System.out.println("🌱 Skill Catalog successfully seeded with reference options.");
            }
        };
    }
}