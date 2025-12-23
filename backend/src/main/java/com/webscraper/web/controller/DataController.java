package com.webscraper.web.controller;

import com.webscraper.application.usecase.data.ExportDataUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

/**
 * Data export controller.
 */
@RestController
@RequestMapping("/api/data")
public class DataController {
    
    private final ExportDataUseCase exportDataUseCase;

    public DataController(ExportDataUseCase exportDataUseCase) {
        this.exportDataUseCase = exportDataUseCase;
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportData(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam UUID targetId,
            @RequestParam(defaultValue = "excel") String format) {
        
        String filename;
        String contentType;
        
        switch (format.toLowerCase()) {
            case "excel", "xlsx" -> {
                filename = "export.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
            case "csv" -> {
                filename = "export.csv";
                contentType = "text/csv";
            }
            default -> {
                filename = "export.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }
        
        StreamingResponseBody stream = output -> {
            var command = new ExportDataUseCase.Command(userId, targetId, format);
            exportDataUseCase.execute(command, output);
        };
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(stream);
    }
}
