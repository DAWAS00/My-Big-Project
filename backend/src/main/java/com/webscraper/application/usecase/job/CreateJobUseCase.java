package com.webscraper.application.usecase.job;

import com.webscraper.application.port.out.JobQueue;
import com.webscraper.application.port.out.JobRepository;
import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.entity.Target;
import com.webscraper.domain.exception.AccessDeniedException;
import com.webscraper.domain.exception.DomainException;
import com.webscraper.domain.exception.EntityNotFoundException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Use case: Create and queue a scraping job.
 */
public class CreateJobUseCase {
    
    private final JobRepository jobRepository;
    private final TargetRepository targetRepository;
    private final JobQueue jobQueue;

    public CreateJobUseCase(JobRepository jobRepository, TargetRepository targetRepository, JobQueue jobQueue) {
        this.jobRepository = jobRepository;
        this.targetRepository = targetRepository;
        this.jobQueue = jobQueue;
    }

    public record Command(
        UUID userId,
        UUID targetId,
        Map<String, Object> config,
        Instant scheduledAt
    ) {}
    
    public record Result(ScrapeJob job) {}

    public Result execute(Command command) {
        // Find target
        Target target = targetRepository.findById(command.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Target", command.targetId()));
        
        // Check ownership
        if (!target.isOwnedBy(command.userId())) {
            throw new AccessDeniedException("Not authorized to create job for this target");
        }
        
        // Check for duplicate pending jobs
        if (!jobRepository.findPendingByTargetId(command.targetId()).isEmpty()) {
            throw new DomainException("A pending job already exists for this target");
        }
        
        // Create job
        ScrapeJob job = ScrapeJob.create(
            command.targetId(),
            command.userId(),
            command.config(),
            command.scheduledAt()
        );
        
        // Save and queue
        ScrapeJob saved = jobRepository.save(job);
        jobQueue.enqueue(saved);
        
        return new Result(saved);
    }
}
