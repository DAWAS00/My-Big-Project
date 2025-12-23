-- ============================================================================
-- V4__scrape_jobs.sql
-- Scraping job execution records
-- ============================================================================

CREATE TABLE scrape_jobs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    target_id       UUID NOT NULL,
    user_id         UUID NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    config          JSONB NOT NULL DEFAULT '{}',
    scheduled_at    TIMESTAMP WITH TIME ZONE,
    started_at      TIMESTAMP WITH TIME ZONE,
    completed_at    TIMESTAMP WITH TIME ZONE,
    pages_found     INTEGER NOT NULL DEFAULT 0,
    pages_scraped   INTEGER NOT NULL DEFAULT 0,
    error_message   TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_jobs_target FOREIGN KEY (target_id) 
        REFERENCES targets(id) ON DELETE CASCADE,
    CONSTRAINT fk_jobs_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT ck_jobs_status CHECK (
        status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED')
    )
);

-- Index for target's jobs
CREATE INDEX idx_jobs_target ON scrape_jobs(target_id);

-- Index for job status filtering
CREATE INDEX idx_jobs_status ON scrape_jobs(status);

-- Index for scheduled jobs (pending only)
CREATE INDEX idx_jobs_scheduled ON scrape_jobs(scheduled_at) 
    WHERE status = 'PENDING' AND scheduled_at IS NOT NULL;

-- Index for recent jobs
CREATE INDEX idx_jobs_created ON scrape_jobs(created_at DESC);

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.columns 
--            WHERE table_name = 'scrape_jobs';
-- Expected: 13 columns
-- ============================================================================
