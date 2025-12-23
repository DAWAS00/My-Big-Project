package com.webscraper.domain.valueobject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Value object representing a URL hash.
 * Used for fast URL lookup/deduplication.
 */
public record UrlHash(String value) {
    
    public UrlHash {
        Objects.requireNonNull(value, "Hash value cannot be null");
    }

    public static UrlHash of(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            return new UrlHash(HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
