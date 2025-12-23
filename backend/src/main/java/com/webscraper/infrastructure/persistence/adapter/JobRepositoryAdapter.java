package com.webscraper.infrastructure.persistence.adapter;

import com.webscraper.application.port.out.JobRepository;
import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.valueobject.JobStatus;
import com.webscraper.infrastructure.persistence.jpa.repository.JpaScrapeJobRepository;
import com.webscraper.infrastructure.persistence.mapper.ScrapeJobMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JobRepositoryAdapter implements JobRepository {
    
    private final JpaScrapeJobRepository jpaRepository;
    private final ScrapeJobMapper mapper;

    public JobRepositoryAdapter(JpaScrapeJobRepository jpaRepository, ScrapeJobMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ScrapeJob save(ScrapeJob job) {
        var entity = mapper.toJpa(job);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ScrapeJob> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ScrapeJob> findByUserId(UUID userId, int page, int size) {
        return jpaRepository.findByUserId(userId, PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public List<ScrapeJob> findByUserIdAndStatus(UUID userId, JobStatus status, int page, int size) {
        return jpaRepository.findByUserIdAndStatus(userId, status.name(), PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public List<ScrapeJob> findByTargetId(UUID targetId) {
        return jpaRepository.findByTargetId(targetId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ScrapeJob> findPendingByTargetId(UUID targetId) {
        return jpaRepository.findPendingByTargetId(targetId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ScrapeJob> findPendingJobsOrderedBySchedule() {
        return jpaRepository.findPendingJobsOrderedBySchedule()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
