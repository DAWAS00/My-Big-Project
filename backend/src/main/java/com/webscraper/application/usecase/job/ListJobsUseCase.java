package com.webscraper.application.usecase.job;

import com.webscraper.application.port.out.JobRepository;
import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.valueobject.JobStatus;

import java.util.List;
import java.util.UUID;

/**
 * Use case: List user's jobs with optional status filter.
 */
public class ListJobsUseCase {
    
    private final JobRepository jobRepository;

    public ListJobsUseCase(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public record Command(UUID userId, JobStatus status, int page, int size) {}
    
    public record Result(List<ScrapeJob> jobs) {}

    public Result execute(Command command) {
        List<ScrapeJob> jobs;
        
        if (command.status() != null) {
            jobs = jobRepository.findByUserIdAndStatus(
                    command.userId(), command.status(), command.page(), command.size());
        } else {
            jobs = jobRepository.findByUserId(command.userId(), command.page(), command.size());
        }
        
        return new Result(jobs);
    }
}
