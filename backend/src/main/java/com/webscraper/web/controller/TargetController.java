package com.webscraper.web.controller;

import com.webscraper.application.usecase.targets.CreateTargetUseCase;
import com.webscraper.application.usecase.targets.GetTargetUseCase;
import com.webscraper.application.usecase.targets.ListTargetsUseCase;
import com.webscraper.web.request.CreateTargetRequest;
import com.webscraper.web.response.ApiResponse;
import com.webscraper.web.response.PagedResponse;
import com.webscraper.web.response.TargetResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Target management controller.
 * Thin controller: validates, calls use-case, returns response.
 */
@RestController
@RequestMapping("/api/targets")
public class TargetController {
    
    private final CreateTargetUseCase createTargetUseCase;
    private final GetTargetUseCase getTargetUseCase;
    private final ListTargetsUseCase listTargetsUseCase;

    public TargetController(
            CreateTargetUseCase createTargetUseCase,
            GetTargetUseCase getTargetUseCase,
            ListTargetsUseCase listTargetsUseCase) {
        this.createTargetUseCase = createTargetUseCase;
        this.getTargetUseCase = getTargetUseCase;
        this.listTargetsUseCase = listTargetsUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TargetResponse>> createTarget(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateTargetRequest request) {
        
        var command = new CreateTargetUseCase.Command(
                userId,
                request.name(),
                request.baseUrl(),
                request.description(),
                request.scrapeConfig()
        );
        
        var result = createTargetUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ok(TargetResponse.from(result.target())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TargetResponse>>> listTargets(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        var command = new ListTargetsUseCase.Command(userId, page, size);
        var result = listTargetsUseCase.execute(command);
        
        List<TargetResponse> targets = result.targets().stream()
                .map(TargetResponse::from)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(targets, page, size, result.total())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TargetResponse>> getTarget(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        
        var command = new GetTargetUseCase.Command(userId, id);
        var result = getTargetUseCase.execute(command);
        
        return ResponseEntity.ok(ApiResponse.ok(TargetResponse.from(result.target())));
    }
}
