package com.webscraper.infrastructure.config;

import com.webscraper.application.port.out.JobQueue;
import com.webscraper.application.port.out.JobRepository;
import com.webscraper.application.port.out.PasswordEncoder;
import com.webscraper.application.port.out.TargetRepository;
import com.webscraper.application.port.out.UserRepository;
import com.webscraper.application.usecase.auth.LoginUserUseCase;
import com.webscraper.application.usecase.auth.RegisterUserUseCase;
import com.webscraper.application.usecase.job.CancelJobUseCase;
import com.webscraper.application.usecase.job.CreateJobUseCase;
import com.webscraper.application.usecase.targets.CreateTargetUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for wiring use-cases with their dependencies.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new LoginUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public CreateTargetUseCase createTargetUseCase(TargetRepository targetRepository) {
        return new CreateTargetUseCase(targetRepository);
    }

    @Bean
    public CreateJobUseCase createJobUseCase(JobRepository jobRepository, TargetRepository targetRepository, JobQueue jobQueue) {
        return new CreateJobUseCase(jobRepository, targetRepository, jobQueue);
    }

    @Bean
    public CancelJobUseCase cancelJobUseCase(JobRepository jobRepository) {
        return new CancelJobUseCase(jobRepository);
    }
}
