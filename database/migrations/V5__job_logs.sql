-- ============================================================================
-- V5__job_logs.sql
-- Job execution logs for debugging
-- ============================================================================

CREATE TABLE job_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id          UUID NOT NULL,
    level           VARCHAR(20) NOT NULL,
    message         TEXT NOT NULL,
    metadata        JSONB DEFAULT '{}',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_logs_job FOREIGN KEY (job_id) 
        REFERENCES scrape_jobs(id) ON DELETE CASCADE,
    CONSTRAINT ck_logs_level CHECK (
        level IN ('DEBUG', 'INFO', 'WARN', 'ERROR')
    )
);

-- Index for job's logs
CREATE INDEX idx_logs_job ON job_logs(job_id);

-- Index for log level filtering
CREATE INDEX idx_logs_level ON job_logs(level);

-- Index for recent logs
CREATE INDEX idx_logs_created ON job_logs(created_at DESC);

-- ============================================================================
-- Done when: SELECT count(*) FROM pg_indexes WHERE tablename = 'job_logs';
-- Expected: 4 (1 PK + 3 custom indexes)
-- ============================================================================
