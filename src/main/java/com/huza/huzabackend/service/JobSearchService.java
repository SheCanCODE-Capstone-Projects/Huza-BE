package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.JobSearchRequest;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobSearchService {

    private final JobRepository jobRepository;

    public List<Job> searchJobs(JobSearchRequest searchRequest) {
        log.info("Searching jobs with filters: {}", searchRequest);

        Specification<Job> spec = Specification.where(JobSpecification.isActive())
                .and(JobSpecification.hasCategory(searchRequest.getCategory()))
                .and(JobSpecification.hasLocation(searchRequest.getLocation()))
                .and(JobSpecification.hasSalaryMin(searchRequest.getSalary()))
                .and(JobSpecification.hasExperienceLevel(searchRequest.getExperienceLevel()))
                .and(JobSpecification.hasContractType(searchRequest.getContractType()));

        return jobRepository.findAll(spec);
    }

    public Job getJobById(Long jobId) {
        log.info("Fetching job details for ID: {}", jobId);

        return jobRepository.findByJobIdAndStatus(jobId, Job.JobStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));
    }

    public List<Job> getAllActiveJobs() {
        return jobRepository.findAll(JobSpecification.isActive());
    }
}
