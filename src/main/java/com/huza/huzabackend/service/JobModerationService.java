package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.JobResponse;

import java.util.List;

public interface JobModerationService {

    List<JobResponse> getPendingJobs();

    JobResponse getJobForReview(Long jobId);

    JobResponse approveJob(Long jobId);

    void removeJob(Long jobId);
}
