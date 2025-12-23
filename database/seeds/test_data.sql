-- ============================================================================
-- Test Seed Data
-- Run after all migrations to verify tables work correctly
-- ============================================================================

-- 1. Create test user
INSERT INTO users (id, email, password_hash, full_name, role)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'testuser@example.com',
    '$2a$10$dummyhash',
    'Test User',
    'ADMIN'
);

-- 2. Create test target
INSERT INTO targets (id, user_id, name, base_url, scrape_config)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'Example Site',
    'https://example.com',
    '{"engine": "playwright", "waitFor": ".content"}'
);

-- 3. Create test job
INSERT INTO scrape_jobs (id, target_id, user_id, status, pages_found, pages_scraped)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'COMPLETED',
    5,
    5
);

-- 4. Create test page
INSERT INTO pages (id, target_id, discovered_by_job_id, url, url_hash, scrape_count)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    '22222222-2222-2222-2222-222222222222',
    '33333333-3333-3333-3333-333333333333',
    'https://example.com/page1',
    'abc123hash',
    1
);

-- 5. Create test page version
INSERT INTO page_versions (id, page_id, job_id, raw_html, content_hash, http_status)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    '44444444-4444-4444-4444-444444444444',
    '33333333-3333-3333-3333-333333333333',
    '<html><body><h1>Example</h1><p>This is test content.</p></body></html>',
    'contenthash123',
    200
);

-- 6. Create test chunk
INSERT INTO chunks (id, page_version_id, content, chunk_index, token_count)
VALUES (
    '66666666-6666-6666-6666-666666666666',
    '55555555-5555-5555-5555-555555555555',
    'This is test content for RAG retrieval.',
    0,
    8
);

-- 7. Create test embedding (1536 zeros as placeholder)
INSERT INTO embeddings (id, chunk_id, embedding, model_name)
VALUES (
    '77777777-7777-7777-7777-777777777777',
    '66666666-6666-6666-6666-666666666666',
    (SELECT array_agg(0)::vector(1536) FROM generate_series(1, 1536)),
    'text-embedding-3-small'
);

-- 8. Create test AI request
INSERT INTO ai_requests (id, user_id, target_id, query, request_type)
VALUES (
    '88888888-8888-8888-8888-888888888888',
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'What is on the example page?',
    'QUERY'
);

-- 9. Create test AI response
INSERT INTO ai_responses (id, request_id, response, tokens_used, latency_ms)
VALUES (
    '99999999-9999-9999-9999-999999999999',
    '88888888-8888-8888-8888-888888888888',
    'The example page contains test content for RAG retrieval.',
    50,
    250
);

-- 10. Create test citation
INSERT INTO citations (response_id, chunk_id, relevance_score, citation_order)
VALUES (
    '99999999-9999-9999-9999-999999999999',
    '66666666-6666-6666-6666-666666666666',
    0.95,
    1
);

-- Verify seed data
SELECT 'users' AS tbl, count(*) FROM users
UNION ALL SELECT 'targets', count(*) FROM targets
UNION ALL SELECT 'scrape_jobs', count(*) FROM scrape_jobs
UNION ALL SELECT 'pages', count(*) FROM pages
UNION ALL SELECT 'page_versions', count(*) FROM page_versions
UNION ALL SELECT 'chunks', count(*) FROM chunks
UNION ALL SELECT 'embeddings', count(*) FROM embeddings
UNION ALL SELECT 'ai_requests', count(*) FROM ai_requests
UNION ALL SELECT 'ai_responses', count(*) FROM ai_responses
UNION ALL SELECT 'citations', count(*) FROM citations;
-- Expected: 1 row in each table
