package com.webscraper.infrastructure.persistence.mapper;

import com.webscraper.domain.entity.User;
import com.webscraper.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between domain User and JPA UserJpaEntity.
 */
@Component
public class UserMapper {
    
    public User toDomain(UserJpaEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getFullName(),
            entity.getRole(),
            entity.getIsActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    public UserJpaEntity toJpa(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFullName(user.getFullName());
        entity.setRole(user.getRole());
        entity.setIsActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
