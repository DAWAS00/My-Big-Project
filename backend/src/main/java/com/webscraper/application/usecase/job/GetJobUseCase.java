package com.webscraper.application.usecase.job;

import com.webscraper.application.port.out.JobRepository;
import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.exception.AccessDeniedException;
import com.webscraper.domain.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Use case: Get a single job by ID.
 */
public class GetJobUseCase {
    
    private final JobRepository jobRepository;

    public GetJobUseCase(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public record Command(UUID userId, UUID jobId) {}
    
    public record Result(ScrapeJob job) {}

    public Result execute(Command command) {
        ScrapeJob job = jobRepository.findById(command.jobId())
                .orElseThrow(() -> new EntityNotFoundException("Job", command.jobId()));
        
        if (!job.isOwnedBy(command.userId())) {
            throw new AccessDeniedException("Not authorized to access this job");
        }
        
        return new Result(job);
    }
}
