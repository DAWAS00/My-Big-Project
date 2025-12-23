-- ============================================================================
-- VERIFICATION CHECKLIST
-- Run these queries after applying all migrations
-- ============================================================================

-- =========================
-- 1. Check all tables exist
-- =========================
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;
-- Expected: 11 tables (users, targets, scrape_jobs, job_logs, pages, 
--           page_versions, chunks, embeddings, ai_requests, ai_responses, citations)

-- ============================
-- 2. Check extensions enabled
-- ============================
SELECT extname, extversion FROM pg_extension WHERE extname IN ('uuid-ossp', 'vector');
-- Expected: 2 rows

-- =====================================
-- 3. Check foreign key constraints
-- =====================================
SELECT 
    tc.table_name, 
    tc.constraint_name, 
    ccu.table_name AS foreign_table
FROM information_schema.table_constraints AS tc 
JOIN information_schema.constraint_column_usage AS ccu 
    ON tc.constraint_name = ccu.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
ORDER BY tc.table_name;
-- Expected: 13 foreign keys

-- ============================
-- 4. Check vector index exists
-- ============================
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE indexname = 'idx_embeddings_vector';
-- Expected: 1 row with 'hnsw' in indexdef

-- ====================
-- 5. Check all views
-- ====================
SELECT table_name 
FROM information_schema.views 
WHERE table_schema = 'public';
-- Expected: 3 views (vw_latest_page_versions, vw_job_stats, vw_chunks_with_embeddings)

-- ============================
-- 6. Check trigger exists
-- ============================
SELECT trigger_name, event_object_table 
FROM information_schema.triggers 
WHERE trigger_schema = 'public';
-- Expected: 2 triggers (users, targets)


-- ============================================================================
-- EXAMPLE QUERIES
-- ============================================================================

-- ===========================================
-- Example 1: Get latest version of each page
-- ===========================================
SELECT * FROM vw_latest_page_versions WHERE target_id = '...' LIMIT 10;

-- Or raw query:
SELECT DISTINCT ON (pv.page_id)
    p.url,
    pv.scraped_at,
    pv.http_status
FROM page_versions pv
JOIN pages p ON pv.page_id = p.id
WHERE p.target_id = 'your-target-uuid'
ORDER BY pv.page_id, pv.scraped_at DESC;

-- =========================================
-- Example 2: Job dashboard stats for a user
-- =========================================
SELECT * FROM vw_job_stats;

-- Or with user filter:
SELECT 
    t.name AS target,
    COUNT(CASE WHEN sj.status = 'COMPLETED' THEN 1 END) AS completed,
    COUNT(CASE WHEN sj.status = 'FAILED' THEN 1 END) AS failed,
    COUNT(CASE WHEN sj.status = 'RUNNING' THEN 1 END) AS running
FROM targets t
LEFT JOIN scrape_jobs sj ON t.id = sj.target_id
WHERE t.user_id = 'your-user-uuid'
GROUP BY t.id, t.name;

-- ================================================
-- Example 3: TopK vector search within a target
-- (Find similar chunks to a query embedding)
-- ================================================
-- Replace $1 with query embedding vector, $2 with target_id, $3 with limit

WITH target_chunks AS (
    SELECT c.id, c.content, e.embedding
    FROM chunks c
    JOIN embeddings e ON c.id = e.chunk_id
    JOIN page_versions pv ON c.page_version_id = pv.id
    JOIN pages p ON pv.page_id = p.id
    WHERE p.target_id = $2  -- Filter by target
)
SELECT 
    id AS chunk_id,
    content,
    1 - (embedding <=> $1) AS similarity_score  -- Cosine similarity
FROM target_chunks
ORDER BY embedding <=> $1  -- Cosine distance (ascending = most similar first)
LIMIT $3;

-- Example with placeholder values:
-- $1 = '[0.1, 0.2, ...]'::vector(1536)
-- $2 = 'target-uuid'
-- $3 = 10

-- ==========================================
-- Example 4: Get AI response with citations
-- ==========================================
SELECT 
    ar.query,
    resp.response,
    resp.tokens_used,
    c.content AS cited_text,
    cit.relevance_score,
    p.url AS source_url
FROM ai_requests ar
JOIN ai_responses resp ON ar.id = resp.request_id
JOIN citations cit ON resp.id = cit.response_id
JOIN chunks c ON cit.chunk_id = c.id
JOIN page_versions pv ON c.page_version_id = pv.id
JOIN pages p ON pv.page_id = p.id
WHERE ar.id = 'request-uuid'
ORDER BY cit.citation_order;

-- ==========================================
-- Example 5: Pages that haven't changed
-- (Same content hash across versions)
-- ==========================================
SELECT 
    p.url,
    COUNT(DISTINCT pv.content_hash) AS unique_versions,
    COUNT(*) AS total_scrapes
FROM pages p
JOIN page_versions pv ON p.id = pv.page_id
GROUP BY p.id, p.url
HAVING COUNT(DISTINCT pv.content_hash) = 1
ORDER BY total_scrapes DESC;
