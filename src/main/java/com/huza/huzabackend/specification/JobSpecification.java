package com.huza.huzabackend.specification;

import com.huza.huzabackend.entity.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobSpecification {

    public static Specification<Job> hasCategory(String categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null || categoryId.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<Job, Category> categoryJoin = root.join("category");
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location == null || location.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("location")),
                "%" + location.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Job> hasSalaryMin(BigDecimal salaryMin) {
        return (root, query, criteriaBuilder) -> {
            if (salaryMin == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("salaryMin"), salaryMin);
        };
    }

    public static Specification<Job> hasExperienceLevel(ExperienceLevel experienceLevel) {
        return (root, query, criteriaBuilder) -> {
            if (experienceLevel == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("experienceLevel"), experienceLevel);
        };
    }

    public static Specification<Job> hasContractType(ContractType contractType) {
        return (root, query, criteriaBuilder) -> {
            if (contractType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("contractType"), contractType);
        };
    }

    public static Specification<Job> isActive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isTrue(root.get("isActive"));
    }
}
