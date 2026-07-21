package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.JobMapper;
import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobModerationServiceImpl implements JobModerationService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public List<JobResponse> getPendingJobs() {
        return jobRepository.findAllByStatusWithDetails(Job.JobStatus.PENDING).stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public JobResponse getJobForReview(Long jobId) {
        return jobMapper.toResponse(findJob(jobId));
    }

    @Override
    @Transactional
    public JobResponse approveJob(Long jobId) {
        Job job = findJob(jobId);
        if (job.getStatus() != Job.JobStatus.PENDING) {
            throw new IllegalStateException("Only pending jobs can be approved");
        }

        job.setStatus(Job.JobStatus.OPEN);
        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void removeJob(Long jobId) {
        jobRepository.delete(findJob(jobId));
    }

    private Job findJob(Long jobId) {
        return jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }
}
