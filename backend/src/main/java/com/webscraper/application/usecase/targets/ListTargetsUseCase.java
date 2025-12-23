package com.webscraper.application.usecase.targets;

import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.Target;

import java.util.List;
import java.util.UUID;

/**
 * Use case: List user's targets with pagination.
 */
public class ListTargetsUseCase {
    
    private final TargetRepository targetRepository;

    public ListTargetsUseCase(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public record Command(UUID userId, int page, int size) {}
    
    public record Result(List<Target> targets, long total) {}

    public Result execute(Command command) {
        List<Target> targets = targetRepository.findByUserId(
                command.userId(), command.page(), command.size());
        long total = targetRepository.countByUserId(command.userId());
        
        return new Result(targets, total);
    }
}
