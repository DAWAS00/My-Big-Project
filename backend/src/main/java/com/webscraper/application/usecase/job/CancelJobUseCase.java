package com.webscraper.application.usecase.job;

import com.webscraper.application.port.out.JobRepository;
import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.exception.AccessDeniedException;
import com.webscraper.domain.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Use case: Cancel a pending or running job.
 */
public class CancelJobUseCase {
    
    private final JobRepository jobRepository;

    public CancelJobUseCase(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public record Command(UUID userId, UUID jobId) {}
    
    public record Result(ScrapeJob job) {}

    public Result execute(Command command) {
        ScrapeJob job = jobRepository.findById(command.jobId())
                .orElseThrow(() -> new EntityNotFoundException("Job", command.jobId()));
        
        if (!job.isOwnedBy(command.userId())) {
            throw new AccessDeniedException("Not authorized to cancel this job");
        }
        
        job.cancel();  // Domain logic handles state validation
        
        ScrapeJob saved = jobRepository.save(job);
        return new Result(saved);
    }
}
