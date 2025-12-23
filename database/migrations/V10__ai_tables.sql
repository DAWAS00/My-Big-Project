-- ============================================================================
-- V10__ai_tables.sql
-- AI requests, responses, and citations
-- ============================================================================

-- AI Requests (user queries)
CREATE TABLE ai_requests (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL,
    target_id       UUID,  -- Optional: scope to specific target
    query           TEXT NOT NULL,
    request_type    VARCHAR(50) NOT NULL DEFAULT 'QUERY',
    model_name      VARCHAR(100) NOT NULL DEFAULT 'gpt-4-turbo',
    temperature     DECIMAL(3,2) DEFAULT 0.7,
    max_tokens      INTEGER DEFAULT 1000,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ai_requests_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ai_requests_target FOREIGN KEY (target_id) 
        REFERENCES targets(id) ON DELETE SET NULL,
    CONSTRAINT ck_ai_request_type CHECK (
        request_type IN ('QUERY', 'SUMMARY', 'COMPARE', 'EXTRACT')
    )
);

CREATE INDEX idx_ai_requests_user ON ai_requests(user_id);
CREATE INDEX idx_ai_requests_target ON ai_requests(target_id);
CREATE INDEX idx_ai_requests_created ON ai_requests(created_at DESC);

-- AI Responses (LLM answers)
CREATE TABLE ai_responses (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    request_id      UUID NOT NULL,
    response        TEXT NOT NULL,
    tokens_used     INTEGER NOT NULL DEFAULT 0,
    latency_ms      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ai_responses_request FOREIGN KEY (request_id) 
        REFERENCES ai_requests(id) ON DELETE CASCADE,
    CONSTRAINT uk_ai_responses_request UNIQUE (request_id)  -- 1:1 relationship
);

CREATE INDEX idx_ai_responses_request ON ai_responses(request_id);

-- Citations (links response to source chunks)
CREATE TABLE citations (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    response_id     UUID NOT NULL,
    chunk_id        UUID NOT NULL,
    relevance_score DECIMAL(5,4) NOT NULL DEFAULT 0.0,
    citation_order  INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_citations_response FOREIGN KEY (response_id) 
        REFERENCES ai_responses(id) ON DELETE CASCADE,
    CONSTRAINT fk_citations_chunk FOREIGN KEY (chunk_id) 
        REFERENCES chunks(id) ON DELETE SET NULL
);

CREATE INDEX idx_citations_response ON citations(response_id);
CREATE INDEX idx_citations_chunk ON citations(chunk_id);
CREATE INDEX idx_citations_order ON citations(response_id, citation_order);

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.tables 
--            WHERE table_name IN ('ai_requests', 'ai_responses', 'citations');
-- Expected: 3
-- ============================================================================
