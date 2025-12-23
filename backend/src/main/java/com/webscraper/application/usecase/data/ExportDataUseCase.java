package com.webscraper.application.usecase.data;

import com.webscraper.application.port.out.ExportService;
import com.webscraper.application.port.out.PageRepository;
import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.Page;
import com.webscraper.domain.exception.AccessDeniedException;
import com.webscraper.domain.exception.DomainException;
import com.webscraper.domain.exception.EntityNotFoundException;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Use case: Export scraped data to Excel or CSV.
 */
public class ExportDataUseCase {
    
    private final PageRepository pageRepository;
    private final TargetRepository targetRepository;
    private final ExportService exportService;

    public ExportDataUseCase(PageRepository pageRepository, TargetRepository targetRepository, 
                             ExportService exportService) {
        this.pageRepository = pageRepository;
        this.targetRepository = targetRepository;
        this.exportService = exportService;
    }

    public record Command(UUID userId, UUID targetId, String format) {}

    public void execute(Command command, OutputStream output) {
        // Verify target ownership
        var target = targetRepository.findById(command.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Target", command.targetId()));
        
        if (!target.isOwnedBy(command.userId())) {
            throw new AccessDeniedException("Not authorized to export this target's data");
        }
        
        // Get pages for target
        List<Page> pages = pageRepository.findByTargetId(command.targetId(), 0, 10000);
        
        // Convert to export format
        List<String> headers = List.of("URL", "Last Scraped", "Scrape Count", "Created At");
        List<Map<String, Object>> data = pages.stream()
                .map(this::pageToRow)
                .toList();
        
        // Export based on format
        switch (command.format().toLowerCase()) {
            case "excel", "xlsx" -> exportService.exportToExcel(data, headers, output);
            case "csv" -> exportService.exportToCsv(data, headers, output);
            default -> throw new DomainException("Unsupported format: " + command.format());
        }
    }

    private Map<String, Object> pageToRow(Page page) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("URL", page.getUrl());
        row.put("Last Scraped", page.getLastScrapedAt());
        row.put("Scrape Count", page.getScrapeCount());
        row.put("Created At", page.getCreatedAt());
        return row;
    }
}
