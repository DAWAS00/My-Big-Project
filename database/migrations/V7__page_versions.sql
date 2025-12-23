-- ============================================================================
-- V7__page_versions.sql
-- Historical snapshots of page content
-- ============================================================================

CREATE TABLE page_versions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    page_id         UUID NOT NULL,
    job_id          UUID,
    raw_html        TEXT NOT NULL,
    content_hash    VARCHAR(64) NOT NULL,
    http_status     INTEGER NOT NULL DEFAULT 200,
    response_time_ms INTEGER,
    scraped_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_versions_page FOREIGN KEY (page_id) 
        REFERENCES pages(id) ON DELETE CASCADE,
    CONSTRAINT fk_versions_job FOREIGN KEY (job_id) 
        REFERENCES scrape_jobs(id) ON DELETE SET NULL
);

-- Index for page's versions
CREATE INDEX idx_versions_page ON page_versions(page_id);

-- Index for job's versions
CREATE INDEX idx_versions_job ON page_versions(job_id);

-- Index for content deduplication
CREATE INDEX idx_versions_content_hash ON page_versions(content_hash);

-- Index for recent versions
CREATE INDEX idx_versions_scraped ON page_versions(scraped_at DESC);

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.table_constraints 
--            WHERE table_name = 'page_versions' AND constraint_type = 'FOREIGN KEY';
-- Expected: 2
-- ============================================================================
