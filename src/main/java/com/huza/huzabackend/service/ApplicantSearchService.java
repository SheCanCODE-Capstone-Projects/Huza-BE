package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ApplicantResponse;
import com.huza.huzabackend.dto.ApplicantSearchRequest;
import com.huza.huzabackend.entity.Application;
import com.huza.huzabackend.entity.ApplicationStatus;
import com.huza.huzabackend.entity.Job;
import com.huza.huzabackend.entity.Portfolio;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.ApplicationRepository;
import com.huza.huzabackend.repository.JobRepository;
import com.huza.huzabackend.repository.PortfolioRepository;
import com.huza.huzabackend.repository.UserRepository;
import com.huza.huzabackend.specification.ApplicationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantSearchService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    /**
     * Get all applicants for a specific job
     */
    @Transactional(readOnly = true)
    public Page<ApplicantResponse> getApplicantsForJob(String jobId, Pageable pageable) {
        log.info("📋 Fetching applicants for job: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        Page<Application> applications = applicationRepository.findByJob(job, pageable);

        return applications.map(this::mapToApplicantResponse);
    }

    /**
     * Search and filter applicants with dynamic criteria
     */
    @Transactional(readOnly = true)
    public Page<ApplicantResponse> searchApplicants(ApplicantSearchRequest request) {
        log.info("🔍 Searching applicants with filters: {}", request);

        Specification<Application> specification = ApplicationSpecification.buildSpecification(request);
        Page<Application> applications = applicationRepository.findAll(specification, request.toPageable());

        return applications.map(this::mapToApplicantResponse);
    }

    /**
     * Search applicants for a specific recruiter
     */
    @Transactional(readOnly = true)
    public Page<ApplicantResponse> searchApplicantsForRecruiter(
            String recruiterId,
            ApplicantSearchRequest request) {

        log.info("🔍 Searching applicants for recruiter: {}", recruiterId);

        Specification<Application> spec = Specification
                .where(ApplicationSpecification.byRecruiterId(recruiterId))
                .and(ApplicationSpecification.buildSpecification(request));

        Page<Application> applications = applicationRepository.findAll(spec, request.toPageable());

        return applications.map(this::mapToApplicantResponse);
    }

    /**
     * Get applicant portfolio
     */
    @Transactional(readOnly = true)
    public List<Portfolio> getApplicantPortfolio(String artistId) {
        log.info("📁 Fetching portfolio for artist: {}", artistId);

        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + artistId));

        return portfolioRepository.findByArtist(artist);
    }

    /**
     * Get applicant details
     */
    @Transactional(readOnly = true)
    public ApplicantResponse getApplicantDetails(String applicationId) {
        log.info("👤 Fetching applicant details for application: {}", applicationId);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        return mapToApplicantResponse(application);
    }

    /**
     * Get application statistics for a job
     */
    @Transactional(readOnly = true)
    public ApplicationStatistics getApplicationStatistics(String jobId) {
        log.info("📊 Fetching application statistics for job: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        long total = applicationRepository.countByJob(job);
        long pending = applicationRepository.countByJobAndStatus(job, ApplicationStatus.PENDING);
        long reviewed = applicationRepository.countByJobAndStatus(job, ApplicationStatus.REVIEWED);
        long shortlisted = applicationRepository.countByJobAndStatus(job, ApplicationStatus.SHORTLISTED);
        long accepted = applicationRepository.countByJobAndStatus(job, ApplicationStatus.ACCEPTED);
        long rejected = applicationRepository.countByJobAndStatus(job, ApplicationStatus.REJECTED);

        return ApplicationStatistics.builder()
                .total(total)
                .pending(pending)
                .reviewed(reviewed)
                .shortlisted(shortlisted)
                .accepted(accepted)
                .rejected(rejected)
                .build();
    }

    /**
     * Map Application entity to ApplicantResponse DTO
     */
    private ApplicantResponse mapToApplicantResponse(Application application) {
        User artist = application.getArtist();

        // Get portfolio count
        int portfolioCount = Math.toIntExact(portfolioRepository.countByArtist(artist));

        return ApplicantResponse.builder()
                .applicationId(application.getId())
                .artistId(artist.getId())
                .artistName(artist.getFullName())
                .artistEmail(artist.getEmail())
                .profilePicture(artist.getProfilePicture())
                .location(artist.getLocation())
                .headline(artist.getHeadline())
                .experienceYears(artist.getExperienceYears())
                .coverLetter(application.getCoverLetter())
                .proposedRate(application.getProposedRate())
                .status(application.getStatus())
                .appliedAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .portfolioCount(portfolioCount)
                // Skills would need to be fetched from artist_skills
                // Average rating would need to be calculated from reviews
                .build();
    }

    /**
     * Application Statistics DTO
     */
    @lombok.Builder
    @lombok.Data
    public static class ApplicationStatistics {
        private long total;
        private long pending;
        private long reviewed;
        private long shortlisted;
        private long accepted;
        private long rejected;
    }
}