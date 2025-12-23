package com.webscraper.infrastructure.persistence.mapper;

import com.webscraper.domain.entity.Target;
import com.webscraper.infrastructure.persistence.jpa.entity.TargetJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TargetMapper {
    
    public Target toDomain(TargetJpaEntity entity) {
        return new Target(
            entity.getId(),
            entity.getUserId(),
            entity.getName(),
            entity.getBaseUrl(),
            entity.getDescription(),
            entity.getScrapeConfig() != null ? entity.getScrapeConfig() : Map.of(),
            entity.getIsActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    public TargetJpaEntity toJpa(Target target) {
        TargetJpaEntity entity = new TargetJpaEntity();
        entity.setId(target.getId());
        entity.setUserId(target.getUserId());
        entity.setName(target.getName());
        entity.setBaseUrl(target.getBaseUrl());
        entity.setDescription(target.getDescription());
        entity.setScrapeConfig(target.getScrapeConfig());
        entity.setIsActive(target.isActive());
        entity.setCreatedAt(target.getCreatedAt());
        entity.setUpdatedAt(target.getUpdatedAt());
        return entity;
    }
}
