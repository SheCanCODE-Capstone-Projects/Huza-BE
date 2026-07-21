package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ApplicationRequest;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Transactional
    public Application applyForJob(String artistId, ApplicationRequest request) {
        log.info("Artist {} applying for job {}", artistId, request.getJobId());

        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        Job job = jobRepository.findByJobIdAndStatus(request.getJobId(), Job.JobStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found or not open with ID: " + request.getJobId()));

        if (applicationRepository.existsByJob_JobIdAndArtist_Id(request.getJobId(), artistId)) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .artist(artist)
                .coverLetter(request.getCoverLetter())
                .resumeUrl(request.getResumeUrl())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApplication.getId());

        return savedApplication;
    }

    @Transactional
    public void withdrawApplication(String applicationId, String artistId) {
        log.info("Artist {} withdrawing application {}", artistId, applicationId);

        Application application = applicationRepository.findByIdAndArtistId(applicationId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        application.withdraw();
        applicationRepository.save(application);

        log.info("Application {} withdrawn successfully", applicationId);
    }

    @Transactional(readOnly = true)
    public List<Application> getApplicationHistory(String artistId) {
        log.info("Fetching application history for artist: {}", artistId);
        return applicationRepository.findByArtistIdOrderByAppliedAtDesc(artistId);
    }

    @Transactional(readOnly = true)
    public ApplicationStatus getApplicationStatus(String applicationId) {
        log.info("Fetching status for application: {}", applicationId);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        return application.getStatus();
    }

    @Transactional(readOnly = true)
    public Application getApplicationById(String applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));
    }
}
