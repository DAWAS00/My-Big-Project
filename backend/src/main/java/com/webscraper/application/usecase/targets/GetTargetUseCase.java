package com.webscraper.application.usecase.targets;

import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.Target;
import com.webscraper.domain.exception.AccessDeniedException;
import com.webscraper.domain.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Use case: Get a single target by ID.
 */
public class GetTargetUseCase {
    
    private final TargetRepository targetRepository;

    public GetTargetUseCase(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public record Command(UUID userId, UUID targetId) {}
    
    public record Result(Target target) {}

    public Result execute(Command command) {
        Target target = targetRepository.findById(command.targetId())
                .orElseThrow(() -> new EntityNotFoundException("Target", command.targetId()));
        
        if (!target.isOwnedBy(command.userId())) {
            throw new AccessDeniedException("Not authorized to access this target");
        }
        
        return new Result(target);
    }
}
