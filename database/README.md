# Database Setup

## Quick Start

```bash
# Start PostgreSQL container
docker-compose up -d

# Wait for healthy status
docker-compose ps

# Run migrations (in order)
psql -h localhost -U postgres -d webscraper -f migrations/V1__extensions.sql
psql -h localhost -U postgres -d webscraper -f migrations/V2__users.sql
psql -h localhost -U postgres -d webscraper -f migrations/V3__targets.sql
psql -h localhost -U postgres -d webscraper -f migrations/V4__scrape_jobs.sql
psql -h localhost -U postgres -d webscraper -f migrations/V5__job_logs.sql
psql -h localhost -U postgres -d webscraper -f migrations/V6__pages.sql
psql -h localhost -U postgres -d webscraper -f migrations/V7__page_versions.sql
psql -h localhost -U postgres -d webscraper -f migrations/V8__chunks.sql
psql -h localhost -U postgres -d webscraper -f migrations/V9__embeddings.sql
psql -h localhost -U postgres -d webscraper -f migrations/V10__ai_tables.sql
psql -h localhost -U postgres -d webscraper -f migrations/V11__views.sql

# Verify
psql -h localhost -U postgres -d webscraper -f verification.sql

# Seed test data (optional)
psql -h localhost -U postgres -d webscraper -f seeds/test_data.sql
```

## Connection Details

| Setting | Value |
|---------|-------|
| Host | localhost |
| Port | 5432 |
| Database | webscraper |
| Username | postgres |
| Password | postgres |

## pgAdmin Access

- URL: http://localhost:5050
- Email: admin@example.com
- Password: admin

## Migrations Order

| Migration | Tables/Objects |
|-----------|---------------|
| V1 | Extensions (uuid-ossp, vector) |
| V2 | users |
| V3 | targets |
| V4 | scrape_jobs |
| V5 | job_logs |
| V6 | pages |
| V7 | page_versions |
| V8 | chunks |
| V9 | embeddings |
| V10 | ai_requests, ai_responses, citations |
| V11 | Views (vw_latest_page_versions, vw_job_stats, vw_chunks_with_embeddings) |

## Files

```
database/
├── docker-compose.yml      # PostgreSQL + pgAdmin
├── README.md               # This file
├── verification.sql        # Verification queries
├── migrations/
│   ├── V1__extensions.sql
│   ├── V2__users.sql
│   ├── V3__targets.sql
│   ├── V4__scrape_jobs.sql
│   ├── V5__job_logs.sql
│   ├── V6__pages.sql
│   ├── V7__page_versions.sql
│   ├── V8__chunks.sql
│   ├── V9__embeddings.sql
│   ├── V10__ai_tables.sql
│   └── V11__views.sql
└── seeds/
    └── test_data.sql       # Sample data for testing
```
