package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.JobMapper;
import com.huza.huzabackend.dto.CreateJobRequest;
import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.dto.UpdateJobRequest;
import com.huza.huzabackend.entity.*;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.*;
import com.huza.huzabackend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final JobMapper jobMapper;
    private final SkillRepository skillRepository; // NEW

    @Override
    @Transactional
    public JobResponse createJob(CreateJobRequest request) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserIdWithDetails(request.getRecruiterUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recruiter profile not found for user ID: " + request.getRecruiterUserId()));

        Category category = resolveSkillBackedCategory(request.getCategoryId()); // CHANGED

        Job job = Job.builder()
                .category(category)
                .company(recruiter.getCompany())
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salaryMin(request.getSalary())
                .salaryMax(request.getSalary())
                .contractType(parseEnum(ContractType.class, request.getContractType()))
                .experienceLevel(parseEnum(ExperienceLevel.class, request.getExperienceLevel()))
                .applicationDeadline(null)
                .postedBy(request.getRecruiterUserId())
                .status(JobStatus.OPEN)
                .build();

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(String.valueOf(request.getCompanyId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + request.getCompanyId()));
            job.setCompany(company);
        }

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, UpdateJobRequest request) {
        Job job = jobRepository.findById(String.valueOf(jobId))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getSalary() != null) {
            job.setSalaryMin(request.getSalary());
            job.setSalaryMax(request.getSalary());
        }
        if (request.getContractType() != null) job.setContractType(parseEnum(ContractType.class, request.getContractType()));
        if (request.getExperienceLevel() != null) job.setExperienceLevel(parseEnum(ExperienceLevel.class, request.getExperienceLevel()));
        if (request.getDeadline() != null) job.setApplicationDeadline(request.getDeadline().atStartOfDay());

        if (request.getCategoryId() != null) {
            job.setCategory(resolveSkillBackedCategory(request.getCategoryId())); // CHANGED
        }

        return jobMapper.toResponse(jobRepository.save(job));
    }

    // NEW — a job's category must actually have at least one skill under it
    private Category resolveSkillBackedCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));

        if (!skillRepository.existsByCategory_Id(categoryId)) {
            throw new IllegalArgumentException(
                    "Category '" + category.getCategoryName() + "' has no skills assigned to it yet — " +
                            "add at least one skill to this category before posting a job under it.");
        }

        return category;
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(String.valueOf(jobId))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public JobResponse closeJob(Long jobId) {
        Job job = jobRepository.findById(String.valueOf(jobId))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        job.setStatus(JobStatus.CLOSED);
        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {
        Job job = jobRepository.findById(String.valueOf(jobId))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs(String status) {
        List<Job> jobs;
        if (status == null || status.trim().isEmpty()) {
            jobs = jobRepository.findAll();
        } else {
            JobStatus jobStatus = parseEnum(JobStatus.class, status);
            jobs = jobRepository.findAll().stream()
                    .filter(job -> job.getStatus() == jobStatus)
                    .collect(Collectors.toList());
        }
        return jobs.stream()
                .map(jobMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByRecruiter(String recruiterUserId) {
        return jobRepository.findAllByPostedBy(recruiterUserId).stream()
                .map(jobMapper::toResponse)
                .collect(Collectors.toList());
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null) return null;
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value '" + value + "' for " + type.getSimpleName());
        }
    }

}