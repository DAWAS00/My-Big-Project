-- ============================================================================
-- V11__views.sql
-- Helpful views for common queries
-- ============================================================================

-- Latest version of each page
CREATE VIEW vw_latest_page_versions AS
SELECT DISTINCT ON (pv.page_id)
    pv.id AS version_id,
    pv.page_id,
    p.url,
    p.target_id,
    pv.content_hash,
    pv.http_status,
    pv.scraped_at
FROM page_versions pv
JOIN pages p ON pv.page_id = p.id
ORDER BY pv.page_id, pv.scraped_at DESC;

-- Job dashboard statistics
CREATE VIEW vw_job_stats AS
SELECT 
    t.id AS target_id,
    t.name AS target_name,
    COUNT(sj.id) AS total_jobs,
    COUNT(CASE WHEN sj.status = 'COMPLETED' THEN 1 END) AS completed_jobs,
    COUNT(CASE WHEN sj.status = 'FAILED' THEN 1 END) AS failed_jobs,
    COUNT(CASE WHEN sj.status = 'RUNNING' THEN 1 END) AS running_jobs,
    SUM(sj.pages_scraped) AS total_pages_scraped,
    MAX(sj.completed_at) AS last_completed_at
FROM targets t
LEFT JOIN scrape_jobs sj ON t.id = sj.target_id
GROUP BY t.id, t.name;

-- Chunk with embedding status
CREATE VIEW vw_chunks_with_embeddings AS
SELECT 
    c.id AS chunk_id,
    c.page_version_id,
    c.chunk_index,
    c.token_count,
    e.id IS NOT NULL AS has_embedding,
    e.model_name
FROM chunks c
LEFT JOIN embeddings e ON c.id = e.chunk_id;

-- ============================================================================
-- Done when: SELECT count(*) FROM information_schema.views 
--            WHERE table_schema = 'public' AND table_name LIKE 'vw_%';
-- Expected: 3
-- ============================================================================
