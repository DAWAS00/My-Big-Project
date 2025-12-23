package com.webscraper.service;

import com.webscraper.dto.response.PageResponse;
import com.webscraper.entity.Page;
import com.webscraper.exception.ResourceNotFoundException;
import com.webscraper.repository.PageRepository;
import com.webscraper.repository.PageVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataService {
    
    private final PageRepository pageRepository;
    private final PageVersionRepository pageVersionRepository;
    private final TargetService targetService;
    
    public org.springframework.data.domain.Page<PageResponse> getPagesByTarget(
            UUID targetId, UUID userId, Pageable pageable) {
        // Verify user owns the target
        targetService.findTargetByIdAndUser(targetId, userId);
        
        return pageRepository.findByTargetId(targetId, pageable)
                .map(page -> PageResponse.from(page, countVersions(page.getId())));
    }
    
    public Page getPageById(UUID pageId) {
        return pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found"));
    }
    
    private int countVersions(UUID pageId) {
        return (int) pageVersionRepository.findByPageIdOrderByScrapedAtDesc(pageId, Pageable.unpaged())
                .getTotalElements();
    }
}
