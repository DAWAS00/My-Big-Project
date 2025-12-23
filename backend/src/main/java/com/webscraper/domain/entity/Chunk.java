package com.webscraper.domain.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing a text chunk for RAG.
 */
public class Chunk {
    private final UUID id;
    private final UUID pageVersionId;
    private final String content;
    private final int chunkIndex;
    private int tokenCount;
    private Map<String, Object> metadata;
    private final Instant createdAt;

    public static Chunk create(UUID pageVersionId, String content, int chunkIndex, int tokenCount) {
        return new Chunk(
            UUID.randomUUID(),
            pageVersionId,
            content,
            chunkIndex,
            tokenCount,
            Map.of(),
            Instant.now()
        );
    }

    public Chunk(UUID id, UUID pageVersionId, String content, int chunkIndex,
                 int tokenCount, Map<String, Object> metadata, Instant createdAt) {
        this.id = id;
        this.pageVersionId = pageVersionId;
        this.content = content;
        this.chunkIndex = chunkIndex;
        this.tokenCount = tokenCount;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPageVersionId() { return pageVersionId; }
    public String getContent() { return content; }
    public int getChunkIndex() { return chunkIndex; }
    public int getTokenCount() { return tokenCount; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Instant getCreatedAt() { return createdAt; }
}
