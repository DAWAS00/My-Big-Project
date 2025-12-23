-- ============================================================================
-- V3__targets.sql
-- Scraping targets (websites/domains to scrape)
-- ============================================================================

CREATE TABLE targets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL,
    name            VARCHAR(255) NOT NULL,
    base_url        TEXT NOT NULL,
    description     TEXT,
    scrape_config   JSONB NOT NULL DEFAULT '{}',
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_targets_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- Index for user's targets
CREATE INDEX idx_targets_user ON targets(user_id);

-- Index for active targets only
CREATE INDEX idx_targets_active ON targets(is_active) WHERE is_active = true;

-- Trigger for updated_at
CREATE TRIGGER trg_targets_updated_at
    BEFORE UPDATE ON targets
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_timestamp();

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.table_constraints 
--            WHERE table_name = 'targets' AND constraint_type = 'FOREIGN KEY';
-- Expected: 1
-- ============================================================================
