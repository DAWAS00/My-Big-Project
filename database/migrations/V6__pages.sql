-- ============================================================================
-- V6__pages.sql
-- Unique pages discovered during scraping
-- ============================================================================

CREATE TABLE pages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    target_id           UUID NOT NULL,
    discovered_by_job_id UUID,
    url                 TEXT NOT NULL,
    url_hash            VARCHAR(64) NOT NULL,
    last_scraped_at     TIMESTAMP WITH TIME ZONE,
    scrape_count        INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pages_target FOREIGN KEY (target_id) 
        REFERENCES targets(id) ON DELETE CASCADE,
    CONSTRAINT fk_pages_discovered_job FOREIGN KEY (discovered_by_job_id) 
        REFERENCES scrape_jobs(id) ON DELETE SET NULL,
    CONSTRAINT uk_pages_url_hash UNIQUE (url_hash)
);

-- Index for target's pages
CREATE INDEX idx_pages_target ON pages(target_id);

-- Index for URL hash lookups (fast dedup check)
CREATE INDEX idx_pages_url_hash ON pages(url_hash);

-- Index for recently scraped
CREATE INDEX idx_pages_last_scraped ON pages(last_scraped_at DESC);

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.table_constraints 
--            WHERE table_name = 'pages' AND constraint_type = 'UNIQUE';
-- Expected: 1
-- ============================================================================
