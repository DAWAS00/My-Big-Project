-- ============================================================================
-- V9__embeddings.sql
-- Vector embeddings for similarity search
-- ============================================================================

CREATE TABLE embeddings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    chunk_id        UUID NOT NULL,
    embedding       vector(1536) NOT NULL,  -- OpenAI text-embedding-3-small dimension
    model_name      VARCHAR(100) NOT NULL DEFAULT 'text-embedding-3-small',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_embeddings_chunk FOREIGN KEY (chunk_id) 
        REFERENCES chunks(id) ON DELETE CASCADE,
    CONSTRAINT uk_embeddings_chunk UNIQUE (chunk_id)  -- 1:1 relationship
);

-- Index for chunk lookup
CREATE INDEX idx_embeddings_chunk ON embeddings(chunk_id);

-- HNSW index for fast vector similarity search
-- HNSW works better than IVFFlat on empty/small tables
CREATE INDEX idx_embeddings_vector ON embeddings 
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- ============================================================================
-- Done when: SELECT indexdef FROM pg_indexes WHERE indexname = 'idx_embeddings_vector';
-- Expected: Contains 'hnsw'
-- ============================================================================
