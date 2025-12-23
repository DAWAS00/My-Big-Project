package com.webscraper.domain.valueobject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Value object representing a content hash (SHA-256).
 * Used for deduplication of page versions.
 */
public record ContentHash(String value) {
    
    public ContentHash {
        Objects.requireNonNull(value, "Hash value cannot be null");
        if (value.length() != 64) {
            throw new IllegalArgumentException("Invalid SHA-256 hash length");
        }
    }

    public static ContentHash of(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return new ContentHash(HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
