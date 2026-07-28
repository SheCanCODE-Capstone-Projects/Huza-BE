package com.huza.huzabackend.service;

import com.huza.huzabackend.Mapper.ApplicationMapper;
import com.huza.huzabackend.dto.ApplicationRequest;
import com.huza.huzabackend.dto.ApplicationResponse;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.exception.DuplicateResourceException;
import com.huza.huzabackend.exception.ResourceNotFoundException;
import com.huza.huzabackend.entity.NotificationType;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;
    private final NotificationService notificationService;

    @Transactional
    public ApplicationResponse applyForJob(String artistId, ApplicationRequest request) {
        final String trimmedArtistId = artistId.trim();
        log.info("Artist {} applying for job {}", trimmedArtistId, request.getJobId());

        User artist = userRepository.findById(trimmedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + trimmedArtistId));

        Job job = jobRepository.findByJobIdAndStatus(request.getJobId(), Job.JobStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found or not open with ID: " + request.getJobId()));

        if (applicationRepository.existsByJob_JobIdAndArtist_Id(request.getJobId(), trimmedArtistId)) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .artist(artist)
                .coverLetter(request.getCoverLetter())
                .resumeUrl(request.getResumeUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApplication.getId());

        return applicationMapper.toResponse(savedApplication);
    }

    @Transactional
    public void withdrawApplication(String applicationId, String artistId) {
        final String trimmedApplicationId = applicationId.trim();
        final String trimmedArtistId = artistId.trim();
        log.info("Artist {} withdrawing application {}", trimmedArtistId, trimmedApplicationId);

        Application application = applicationRepository.findByIdAndArtistId(trimmedApplicationId, trimmedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + trimmedApplicationId));

        application.withdraw();
        applicationRepository.save(application);

        log.info("Application {} withdrawn successfully", trimmedApplicationId);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationHistory(String artistId) {
        artistId = artistId.trim();
        log.info("Fetching application history for artist: {}", artistId);
        return applicationRepository.findByArtistIdOrderByAppliedAtDesc(artistId).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

//    @Transactional(readOnly = true)
//    public ApplicationResponse getApplicationStatus(String applicationId, String artistId) {
//        applicationId = applicationId.trim();
//        artistId = artistId.trim();
//        log.info("Fetching status for application: {}", applicationId);
//        Application application = applicationRepository.findByIdAndArtistId(applicationId, artistId)
//                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));
//        return applicationMapper.toResponse(application);
//    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationStatus(String applicationId, String artistId) {
        final String trimmedApplicationId = applicationId.trim();
        final String trimmedArtistId = artistId.trim();
        log.info("Fetching status for application: {}", trimmedApplicationId);
        Application application = applicationRepository.findByIdAndArtistId(trimmedApplicationId, trimmedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + trimmedApplicationId));
        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(String applicationId, String artistId) {
        final String trimmedApplicationId = applicationId.trim();
        final String trimmedArtistId = artistId.trim();
        Application application = applicationRepository.findByIdAndArtistId(trimmedApplicationId, trimmedArtistId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + trimmedApplicationId));
        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicantsForJob(Long jobId, String recruiterUserId) {
        final String trimmedRecruiterId = recruiterUserId.trim();
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        validateRecruiterOwnsJob(job, trimmedRecruiterId);

        return applicationRepository.findAllByJobIdWithDetails(jobId).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<ApplicationResponse> assignJobToApplicants(Long jobId, String recruiterUserId, List<String> selectedApplicationIds) {
        recruiterUserId = recruiterUserId.trim();
        if (selectedApplicationIds != null) {
            selectedApplicationIds = selectedApplicationIds.stream().map(String::trim).toList();
        }
        if (selectedApplicationIds == null || selectedApplicationIds.isEmpty() || selectedApplicationIds.size() > 2) {
            throw new IllegalStateException("You must select one or two applicants");
        }

        Set<String> uniqueSelected = new HashSet<>(selectedApplicationIds);
        if (uniqueSelected.size() != selectedApplicationIds.size()) {
            throw new IllegalStateException("Duplicate application IDs are not allowed");
        }

        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        validateRecruiterOwnsJob(job, recruiterUserId);

        if (job.getStatus() != Job.JobStatus.OPEN) {
            throw new IllegalStateException("Only OPEN jobs can be assigned");
        }

        List<Application> applications = applicationRepository.findAllByJobIdWithDetails(jobId);
        if (applications.isEmpty()) {
            throw new IllegalStateException("No applicants found for this job");
        }

        List<Application> pendingApplications = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .toList();
        if (pendingApplications.isEmpty()) {
            throw new IllegalStateException("No pending applicants available for assignment");
        }

        Set<String> pendingIds = pendingApplications.stream().map(Application::getId).collect(java.util.stream.Collectors.toSet());
        if (!pendingIds.containsAll(uniqueSelected)) {
            throw new IllegalStateException("Selected applicants must belong to this job and be in PENDING status");
        }

        for (Application application : pendingApplications) {
            if (uniqueSelected.contains(application.getId())) {
                application.accept(recruiterUserId);
                if (application.getArtist() != null && application.getArtist().getId() != null) {
                    notificationService.notify(
                            application.getArtist().getId(),
                            "Job Assigned!",
                            "Congratulations! You have been assigned the job: " + job.getTitle(),
                            "/applications/" + application.getId(),
                            NotificationType.APPLICATION
                    );
                }
            } else {
                application.reject(recruiterUserId);
                if (application.getArtist() != null && application.getArtist().getId() != null) {
                    notificationService.notify(
                            application.getArtist().getId(),
                            "Application Update",
                            "Your application for job: " + job.getTitle() + " was not selected.",
                            "/applications/" + application.getId(),
                            NotificationType.APPLICATION
                    );
                }
            }
        }

        applicationRepository.saveAll(pendingApplications);
        job.setStatus(Job.JobStatus.CLOSED);
        jobRepository.save(job);

        return pendingApplications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.ACCEPTED)
                .map(applicationMapper::toResponse)
                .toList();
    }

    private void validateRecruiterOwnsJob(Job job, String recruiterUserId) {
        if (!job.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new IllegalStateException("You can only manage applications for your own jobs");
        }
    }
}
