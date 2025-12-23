package com.webscraper.application.usecase.targets;

import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.domain.entity.Target;

import java.util.Map;
import java.util.UUID;

/**
 * Use case: Create a new scraping target.
 */
public class CreateTargetUseCase {
    
    private final TargetRepository targetRepository;

    public CreateTargetUseCase(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public record Command(
        UUID userId,
        String name,
        String baseUrl,
        String description,
        Map<String, Object> scrapeConfig
    ) {}
    
    public record Result(Target target) {}

    public Result execute(Command command) {
        Target target = Target.create(
            command.userId(),
            command.name(),
            command.baseUrl(),
            command.description(),
            command.scrapeConfig()
        );

        Target saved = targetRepository.save(target);
        return new Result(saved);
    }
}
