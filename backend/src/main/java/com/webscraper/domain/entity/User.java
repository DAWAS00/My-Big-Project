package com.webscraper.domain.entity;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing a user.
 * Pure Java - no framework dependencies.
 */
public class User {
    private final UUID id;
    private String email;
    private String passwordHash;
    private String fullName;
    private String role;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    // Factory method for new users
    public static User create(String email, String passwordHash, String fullName) {
        return new User(
            UUID.randomUUID(),
            email,
            passwordHash,
            fullName,
            "USER",
            true,
            Instant.now(),
            Instant.now()
        );
    }

    // Reconstitution from persistence
    public User(UUID id, String email, String passwordHash, String fullName,
                String role, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Domain behavior
    public void updateProfile(String fullName) {
        this.fullName = fullName;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
