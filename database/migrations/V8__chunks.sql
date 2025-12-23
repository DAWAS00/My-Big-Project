-- ============================================================================
-- V8__chunks.sql
-- Text chunks for RAG retrieval
-- ============================================================================

CREATE TABLE chunks (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    page_version_id     UUID NOT NULL,
    content             TEXT NOT NULL,
    chunk_index         INTEGER NOT NULL,
    token_count         INTEGER NOT NULL DEFAULT 0,
    metadata            JSONB DEFAULT '{}',
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_chunks_version FOREIGN KEY (page_version_id) 
        REFERENCES page_versions(id) ON DELETE CASCADE
);

-- Index for version's chunks
CREATE INDEX idx_chunks_version ON chunks(page_version_id);

-- Index for chunk ordering
CREATE INDEX idx_chunks_order ON chunks(page_version_id, chunk_index);

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.columns 
--            WHERE table_name = 'chunks';
-- Expected: 7 columns
-- ============================================================================
