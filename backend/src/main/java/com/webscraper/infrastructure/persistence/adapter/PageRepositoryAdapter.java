package com.webscraper.infrastructure.persistence.adapter;

import com.webscraper.application.port.out.PageRepository;
import com.webscraper.domain.entity.Page;
import com.webscraper.domain.valueobject.UrlHash;
import com.webscraper.infrastructure.persistence.jpa.repository.JpaPageRepository;
import com.webscraper.infrastructure.persistence.mapper.PageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PageRepositoryAdapter implements PageRepository {
    
    private final JpaPageRepository jpaRepository;
    private final PageMapper mapper;

    public PageRepositoryAdapter(JpaPageRepository jpaRepository, PageMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page save(Page page) {
        var entity = mapper.toJpa(page);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Page> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Page> findByUrlHash(UrlHash urlHash) {
        return jpaRepository.findByUrlHash(urlHash.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUrlHash(UrlHash urlHash) {
        return jpaRepository.existsByUrlHash(urlHash.value());
    }

    @Override
    public List<Page> findByTargetId(UUID targetId, int page, int size) {
        return jpaRepository.findByTargetId(targetId, PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public long countByTargetId(UUID targetId) {
        return jpaRepository.countByTargetId(targetId);
    }
}
