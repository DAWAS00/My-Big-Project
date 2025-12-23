package com.webscraper.infrastructure.persistence.adapter;

import com.webscraper.application.port.out.UserRepository;
import com.webscraper.domain.entity.User;
import com.webscraper.infrastructure.persistence.jpa.repository.JpaUserRepository;
import com.webscraper.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the UserRepository port.
 * Bridges domain layer with JPA infrastructure.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {
    
    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        var entity = mapper.toJpa(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
