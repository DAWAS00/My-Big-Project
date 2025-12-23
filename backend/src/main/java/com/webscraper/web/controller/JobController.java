package com.webscraper.web.controller;

import com.webscraper.application.usecase.job.CancelJobUseCase;
import com.webscraper.application.usecase.job.CreateJobUseCase;
import com.webscraper.application.usecase.job.GetJobUseCase;
import com.webscraper.application.usecase.job.ListJobsUseCase;
import com.webscraper.domain.valueobject.JobStatus;
import com.webscraper.web.request.CreateJobRequest;
import com.webscraper.web.response.ApiResponse;
import com.webscraper.web.response.JobResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Job management controller.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    private final CreateJobUseCase createJobUseCase;
    private final GetJobUseCase getJobUseCase;
    private final ListJobsUseCase listJobsUseCase;
    private final CancelJobUseCase cancelJobUseCase;

    public JobController(
            CreateJobUseCase createJobUseCase,
            GetJobUseCase getJobUseCase,
            ListJobsUseCase listJobsUseCase,
            CancelJobUseCase cancelJobUseCase) {
        this.createJobUseCase = createJobUseCase;
        this.getJobUseCase = getJobUseCase;
        this.listJobsUseCase = listJobsUseCase;
        this.cancelJobUseCase = cancelJobUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateJobRequest request) {
        
        var command = new CreateJobUseCase.Command(
                userId,
                request.targetId(),
                request.config(),
                request.scheduledAt()
        );
        
        var result = createJobUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ok(JobResponse.from(result.job())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> listJobs(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        JobStatus jobStatus = status != null ? JobStatus.valueOf(status.toUpperCase()) : null;
        var command = new ListJobsUseCase.Command(userId, jobStatus, page, size);
        var result = listJobsUseCase.execute(command);
        
        List<JobResponse> jobs = result.jobs().stream()
                .map(JobResponse::from)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        
        var command = new GetJobUseCase.Command(userId, id);
        var result = getJobUseCase.execute(command);
        
        return ResponseEntity.ok(ApiResponse.ok(JobResponse.from(result.job())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> cancelJob(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        
        var command = new CancelJobUseCase.Command(userId, id);
        var result = cancelJobUseCase.execute(command);
        
        return ResponseEntity.ok(ApiResponse.ok(JobResponse.from(result.job())));
    }
}
