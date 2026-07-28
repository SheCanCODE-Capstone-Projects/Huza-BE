package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.CreateJobRequest;
import com.huza.huzabackend.dto.JobResponse;
import com.huza.huzabackend.dto.UpdateJobRequest;

import java.util.List;

public interface JobService {
    JobResponse createJob(CreateJobRequest request);
    JobResponse updateJob(Long jobId, UpdateJobRequest request);
    void deleteJob(Long jobId);
    JobResponse closeJob(Long jobId);
    JobResponse getJob(Long jobId);
    List<JobResponse> getJobsByRecruiter(String recruiterUserId);
    List<JobResponse> getAllJobs(String status);
}