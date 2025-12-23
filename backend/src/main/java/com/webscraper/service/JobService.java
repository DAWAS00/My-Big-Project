package com.webscraper.service;

import com.webscraper.dto.request.CreateJobRequest;
import com.webscraper.dto.response.JobResponse;
import com.webscraper.entity.ScrapeJob;
import com.webscraper.entity.Target;
import com.webscraper.entity.User;
import com.webscraper.exception.ResourceNotFoundException;
import com.webscraper.exception.ValidationException;
import com.webscraper.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    
    private final JobRepository jobRepository;
    private final TargetService targetService;
    
    @Transactional
    public JobResponse createJob(CreateJobRequest request, User user) {
        Target target = targetService.findTargetByIdAndUser(request.getTargetId(), user.getId());
        
        // Check for duplicate pending jobs
        if (!jobRepository.findPendingJobsByTarget(target.getId()).isEmpty()) {
            throw new ValidationException("A pending job already exists for this target");
        }
        
        ScrapeJob job = ScrapeJob.builder()
                .target(target)
                .user(user)
                .config(request.getConfig() != null ? request.getConfig() : Map.of())
                .scheduledAt(request.getScheduledAt())
                .build();
        
        job = jobRepository.save(job);
        return JobResponse.from(job);
    }
    
    public Page<JobResponse> getUserJobs(UUID userId, ScrapeJob.Status status, Pageable pageable) {
        if (status != null) {
            return jobRepository.findByUserIdAndStatus(userId, status, pageable)
                    .map(JobResponse::from);
        }
        return jobRepository.findByUserId(userId, pageable)
                .map(JobResponse::from);
    }
    
    public JobResponse getJobById(UUID jobId, UUID userId) {
        ScrapeJob job = findJobByIdAndUser(jobId, userId);
        return JobResponse.from(job);
    }
    
    @Transactional
    public JobResponse cancelJob(UUID jobId, UUID userId) {
        ScrapeJob job = findJobByIdAndUser(jobId, userId);
        
        if (job.getStatus() != ScrapeJob.Status.PENDING && job.getStatus() != ScrapeJob.Status.RUNNING) {
            throw new ValidationException("Can only cancel PENDING or RUNNING jobs");
        }
        
        job.setStatus(ScrapeJob.Status.CANCELLED);
        job = jobRepository.save(job);
        return JobResponse.from(job);
    }
    
    private ScrapeJob findJobByIdAndUser(UUID jobId, UUID userId) {
        return jobRepository.findById(jobId)
                .filter(j -> j.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }
}
