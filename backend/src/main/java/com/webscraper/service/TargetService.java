package com.webscraper.service;

import com.webscraper.dto.request.CreateTargetRequest;
import com.webscraper.dto.response.TargetResponse;
import com.webscraper.entity.Target;
import com.webscraper.entity.User;
import com.webscraper.exception.ResourceNotFoundException;
import com.webscraper.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TargetService {
    
    private final TargetRepository targetRepository;
    
    @Transactional
    public TargetResponse createTarget(CreateTargetRequest request, User user) {
        Target target = Target.builder()
                .user(user)
                .name(request.getName())
                .baseUrl(request.getBaseUrl())
                .description(request.getDescription())
                .scrapeConfig(request.getScrapeConfig() != null ? request.getScrapeConfig() : Map.of())
                .build();
        
        target = targetRepository.save(target);
        return TargetResponse.from(target);
    }
    
    public Page<TargetResponse> getUserTargets(UUID userId, Pageable pageable) {
        return targetRepository.findByUserIdAndIsActiveTrue(userId, pageable)
                .map(TargetResponse::from);
    }
    
    public TargetResponse getTargetById(UUID targetId, UUID userId) {
        Target target = findTargetByIdAndUser(targetId, userId);
        return TargetResponse.from(target);
    }
    
    @Transactional
    public TargetResponse updateTarget(UUID targetId, CreateTargetRequest request, UUID userId) {
        Target target = findTargetByIdAndUser(targetId, userId);
        
        target.setName(request.getName());
        target.setBaseUrl(request.getBaseUrl());
        target.setDescription(request.getDescription());
        if (request.getScrapeConfig() != null) {
            target.setScrapeConfig(request.getScrapeConfig());
        }
        
        target = targetRepository.save(target);
        return TargetResponse.from(target);
    }
    
    @Transactional
    public void deleteTarget(UUID targetId, UUID userId) {
        Target target = findTargetByIdAndUser(targetId, userId);
        target.setIsActive(false);
        targetRepository.save(target);
    }
    
    public Target findTargetByIdAndUser(UUID targetId, UUID userId) {
        return targetRepository.findById(targetId)
                .filter(t -> t.getUser().getId().equals(userId) && t.getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Target not found"));
    }
}
