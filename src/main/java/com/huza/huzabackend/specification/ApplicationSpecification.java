package com.huza.huzabackend.specification;

import com.huza.huzabackend.dto.ApplicantSearchRequest;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSpecification {

    private ApplicationSpecification() {
        // utility class
    }

    /**
     * Builds a dynamic specification from an ApplicantSearchRequest,
     * ignoring any fields that are null/blank.
     */
    public static Specification<Application> buildSpecification(ApplicantSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getJobId())) {
                predicates.add(cb.equal(root.get("job").get("id"), request.getJobId()));
            }

            if (StringUtils.hasText(request.getArtistId())) {
                predicates.add(cb.equal(root.get("artist").get("id"), request.getArtistId()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getMinProposedRate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("proposedRate"), request.getMinProposedRate()));
            }

            if (request.getMaxProposedRate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("proposedRate"), request.getMaxProposedRate()));
            }

            if (StringUtils.hasText(request.getKeyword())) {
                String likePattern = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate coverLetterMatch = cb.like(cb.lower(root.get("coverLetter")), likePattern);
                Predicate nameMatch = cb.like(cb.lower(root.get("artist").get("fullName")), likePattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("artist").get("email")), likePattern);
                predicates.add(cb.or(coverLetterMatch, nameMatch, emailMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Restricts results to applications for jobs posted by a given recruiter.
     */
    public static Specification<Application> byRecruiterId(String recruiterId) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(recruiterId)) {
                return cb.conjunction();
            }
            return cb.equal(root.get("job").get("postedBy"), recruiterId);
        };
    }

    public static Specification<Application> byJob(Job job) {
        return (root, query, cb) -> cb.equal(root.get("job"), job);
    }

    public static Specification<Application> byArtist(User artist) {
        return (root, query, cb) -> cb.equal(root.get("artist"), artist);
    }

    public static Specification<Application> byStatus(Application.ApplicationStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}