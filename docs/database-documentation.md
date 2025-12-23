# Database Documentation

## Web Scraping + AI/RAG System

---

## 1. Purpose of Each Table

### Core Tables

| Table | Purpose | Scraping/RAG Role |
|-------|---------|-------------------|
| **users** | User accounts with RBAC | Owns targets, creates jobs, submits AI queries |
| **targets** | Websites to scrape | Defines scraping scope for a domain |
| **scrape_jobs** | Job execution records | Tracks each scraping run with status |
| **job_logs** | Debugging logs | Stores INFO/WARN/ERROR per job |

### Scraping Pipeline

| Table | Purpose | Scraping/RAG Role |
|-------|---------|-------------------|
| **pages** | Unique URLs discovered | Dedup layer; each URL appears once per target |
| **page_versions** | Historical snapshots | Stores HTML + content_hash for change detection |

### RAG Pipeline

| Table | Purpose | Scraping/RAG Role |
|-------|---------|-------------------|
| **chunks** | Text segments (512 tokens) | Split from page content for retrieval |
| **embeddings** | Vector representations | Enables similarity search via pgvector |
| **ai_requests** | User queries | Logs what users ask |
| **ai_responses** | LLM answers | Stores generated responses |
| **citations** | Source references | Links responses to source chunks (provenance) |

---

## 2. Data Lifecycle

### Stage 1: Target Creation
```
User → POST /api/targets → INSERT INTO targets
```
- User defines `base_url` and `scrape_config` (selectors, wait rules)
- Target becomes eligible for job scheduling

### Stage 2: Job Creation
```
User/Scheduler → POST /api/scrape/jobs → INSERT INTO scrape_jobs (status='PENDING')
```
- Job references target and user
- Worker picks up `PENDING` jobs ordered by `scheduled_at`

### Stage 3: Scraping Results
```
Python Worker → Scrape URL → Store results
```
1. **Discover page**: Check if URL exists via `url_hash`
   - If new: `INSERT INTO pages`
   - If exists: `UPDATE pages SET scrape_count = scrape_count + 1`

2. **Store version**: Always insert new version
   - `INSERT INTO page_versions (content_hash, raw_html)`

### Stage 4: Versioning via content_hash
```
SHA256(raw_html) → content_hash
```
- Compare new `content_hash` to latest version
- **Same hash** = page unchanged, skip chunking
- **Different hash** = page changed, proceed to chunking

### Stage 5: Chunking
```
HTML → Text → Split → INSERT INTO chunks
```
- Extract text from HTML (strip tags)
- Split into ~512 token chunks with overlap
- Store `chunk_index` for ordering

### Stage 6: Embeddings
```
Chunk text → Embedding model → INSERT INTO embeddings
```
- Generate vector via OpenAI or local model
- Store with `model_name` for tracking
- HNSW index enables fast similarity search

### Stage 7: AI Request/Response Logging
```
User query → Vector search → LLM → Store results
```
1. `INSERT INTO ai_requests` (query, target_id)
2. Find similar chunks via vector distance
3. Generate response with LLM
4. `INSERT INTO ai_responses` (response, tokens_used)
5. `INSERT INTO citations` (chunk_id, relevance_score)

---

## 3. Key Rules

### When to Create New page_version vs Update

| Scenario | Action |
|----------|--------|
| First scrape of URL | Create page + page_version |
| Same content_hash | Update `pages.last_scraped_at` only |
| Different content_hash | Create new page_version |

```sql
-- Pseudocode
IF content_hash = (SELECT content_hash FROM page_versions WHERE page_id = ? ORDER BY scraped_at DESC LIMIT 1)
THEN UPDATE pages SET last_scraped_at = NOW()
ELSE INSERT INTO page_versions (...)
END
```

### Idempotency Rules for Jobs

| Rule | Implementation |
|------|----------------|
| No duplicate pending jobs | Check: `SELECT 1 FROM scrape_jobs WHERE target_id = ? AND status = 'PENDING'` |
| Job completion is final | Once `COMPLETED`/`FAILED`, status cannot change |
| Retry logic | On failure, increment `retry_count`, set to `PENDING` if < max_retries |

### Deduplication Rules

| Entity | Dedup Key | Hash Column |
|--------|-----------|-------------|
| Pages | URL | `url_hash = SHA256(url)` |
| Page Versions | Content | `content_hash = SHA256(raw_html)` |
| Chunks | Not deduped | Each version gets fresh chunks |

---

## 4. Citation Design

### Requirements
- Every AI response must link to source chunks
- User can click citation to see original page + version

### Schema
```sql
citations (
    response_id  → ai_responses.id,
    chunk_id     → chunks.id,
    relevance_score DECIMAL(5,4),
    citation_order  INTEGER
)
```

### Full Citation Query
```sql
SELECT 
    ar.query,
    resp.response,
    c.content AS cited_text,
    cit.relevance_score,
    p.url AS source_url,
    pv.scraped_at AS version_date,
    pv.id AS version_id
FROM ai_requests ar
JOIN ai_responses resp ON ar.id = resp.request_id
JOIN citations cit ON resp.id = cit.response_id
JOIN chunks c ON cit.chunk_id = c.id
JOIN page_versions pv ON c.page_version_id = pv.id
JOIN pages p ON pv.page_id = p.id
WHERE ar.id = $1
ORDER BY cit.citation_order;
```

### Citation Display Format
```
[1] "Chunk text..." 
    Source: example.com/page (scraped 2024-01-15)
```

---

## 5. Performance Notes

### Indexes (Already Created)

| Table | Index | Purpose |
|-------|-------|---------|
| users | idx_users_email | Login lookup |
| pages | idx_pages_url_hash | Fast dedup check |
| page_versions | idx_versions_page | Get versions for page |
| page_versions | idx_versions_content_hash | Detect unchanged pages |
| embeddings | idx_embeddings_vector (HNSW) | Vector similarity search |
| citations | idx_citations_response | Get citations for response |

### Additional Indexes to Consider

```sql
-- Composite index for active targets by user
CREATE INDEX idx_targets_user_active ON targets(user_id) WHERE is_active = true;

-- Partial index for pending jobs only
CREATE INDEX idx_jobs_pending ON scrape_jobs(scheduled_at) WHERE status = 'PENDING';

-- GIN index for JSONB config search
CREATE INDEX idx_targets_config ON targets USING gin(scrape_config);
```

### Partitioning Suggestions (If Data Grows)

| Table | Partition Strategy | Trigger |
|-------|-------------------|---------|
| page_versions | By `scraped_at` (monthly) | > 10M rows |
| job_logs | By `created_at` (weekly) | > 5M rows |
| chunks | By `created_at` (monthly) | > 50M rows |
| ai_requests | By `created_at` (monthly) | > 1M rows |

```sql
-- Example: Partition page_versions by month
CREATE TABLE page_versions (
    ...
) PARTITION BY RANGE (scraped_at);

CREATE TABLE page_versions_2024_01 PARTITION OF page_versions
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

### Large HTML Storage Strategy

For pages with HTML > 1MB, store in cloud storage:

```sql
-- Modified page_versions schema
ALTER TABLE page_versions ADD COLUMN raw_html_uri TEXT;
ALTER TABLE page_versions ALTER COLUMN raw_html DROP NOT NULL;

-- Logic:
-- IF size(raw_html) > 1MB:
--   Upload to gs://bucket/versions/{version_id}.html
--   Store raw_html_uri = 'gs://bucket/versions/{version_id}.html'
--   Set raw_html = NULL
-- ELSE:
--   Store raw_html directly
```

| Storage | When | Access |
|---------|------|--------|
| PostgreSQL | HTML < 1MB | Direct query |
| Cloud Storage | HTML >= 1MB | Fetch via URI |

---

## ER Diagram

```mermaid
erDiagram
    USERS ||--o{ TARGETS : owns
    USERS ||--o{ SCRAPE_JOBS : creates
    USERS ||--o{ AI_REQUESTS : submits
    
    TARGETS ||--o{ SCRAPE_JOBS : has
    TARGETS ||--o{ PAGES : contains
    
    SCRAPE_JOBS ||--o{ JOB_LOGS : generates
    SCRAPE_JOBS ||--o{ PAGE_VERSIONS : creates
    
    PAGES ||--o{ PAGE_VERSIONS : has
    
    PAGE_VERSIONS ||--o{ CHUNKS : splits
    
    CHUNKS ||--|| EMBEDDINGS : embeds
    CHUNKS ||--o{ CITATIONS : cited_by
    
    AI_REQUESTS ||--|| AI_RESPONSES : produces
    AI_RESPONSES ||--o{ CITATIONS : has
    
    USERS {
        uuid id PK
        varchar email UK
        varchar role
    }
    
    TARGETS {
        uuid id PK
        uuid user_id FK
        text base_url
        jsonb scrape_config
    }
    
    SCRAPE_JOBS {
        uuid id PK
        uuid target_id FK
        varchar status
    }
    
    PAGES {
        uuid id PK
        uuid target_id FK
        varchar url_hash UK
    }
    
    PAGE_VERSIONS {
        uuid id PK
        uuid page_id FK
        varchar content_hash
        text raw_html
    }
    
    CHUNKS {
        uuid id PK
        uuid page_version_id FK
        text content
        int chunk_index
    }
    
    EMBEDDINGS {
        uuid id PK
        uuid chunk_id FK UK
        vector embedding
    }
    
    AI_REQUESTS {
        uuid id PK
        uuid user_id FK
        text query
    }
    
    AI_RESPONSES {
        uuid id PK
        uuid request_id FK UK
        text response
    }
    
    CITATIONS {
        uuid id PK
        uuid response_id FK
        uuid chunk_id FK
        float relevance_score
    }
    
    JOB_LOGS {
        uuid id PK
        uuid job_id FK
        varchar level
    }
```

---

## Quick Reference

| Action | Tables Affected |
|--------|-----------------|
| Create target | targets |
| Run scrape job | scrape_jobs → pages → page_versions → job_logs |
| Process for RAG | chunks → embeddings |
| AI query | ai_requests → ai_responses → citations |
| View citation | citations → chunks → page_versions → pages |
