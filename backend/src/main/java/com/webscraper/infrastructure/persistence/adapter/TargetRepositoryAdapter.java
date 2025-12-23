package com.webscraper.infrastructure.persistence.adapter;

import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.Target;
import com.webscraper.infrastructure.persistence.jpa.repository.JpaTargetRepository;
import com.webscraper.infrastructure.persistence.mapper.TargetMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TargetRepositoryAdapter implements TargetRepository {
    
    private final JpaTargetRepository jpaRepository;
    private final TargetMapper mapper;

    public TargetRepositoryAdapter(JpaTargetRepository jpaRepository, TargetMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Target save(Target target) {
        var entity = mapper.toJpa(target);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Target> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Target> findByUserId(UUID userId, int page, int size) {
        return jpaRepository.findByUserIdAndIsActiveTrue(userId, PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public List<Target> findActiveByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
