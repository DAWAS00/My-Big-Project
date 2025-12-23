# Sprint 1: Database Design Document

## 📅 Day 1-2 | Database Foundation

---

## Functional Requirements (FR)

| ID | Requirement | Table |
|----|-------------|-------|
| FR-DB-01 | Store user accounts with authentication | users |
| FR-DB-02 | Track scraping jobs with configuration | scraping_jobs |
| FR-DB-03 | Store raw and parsed scraped data | scraped_data |
| FR-DB-04 | Log job execution events | job_logs |
| FR-DB-05 | Store AI analysis results | ai_analyses |
| FR-DB-06 | Support vector similarity search | scraped_data.embedding |

---

## Non-Functional Requirements (NFR)

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-DB-01 | Query response time | < 100ms |
| NFR-DB-02 | Support concurrent connections | 50+ |
| NFR-DB-03 | Data retention | 90 days |
| NFR-DB-04 | Backup frequency | Daily |
| NFR-DB-05 | Vector search performance | < 500ms for 100k rows |

---

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USERS ||--o{ SCRAPING_JOBS : creates
    USERS ||--o{ AI_ANALYSES : requests
    SCRAPING_JOBS ||--o{ SCRAPED_DATA : produces
    SCRAPING_JOBS ||--o{ JOB_LOGS : generates
    SCRAPED_DATA ||--o{ AI_ANALYSES : analyzed_by
    
    USERS {
        uuid id PK
        varchar email UK
        varchar password_hash
        varchar full_name
        varchar role
        boolean is_active
        timestamp created_at
    }
    
    SCRAPING_JOBS {
        uuid id PK
        uuid user_id FK
        varchar target_url
        varchar status
        jsonb config
        timestamp scheduled_at
        timestamp completed_at
    }
    
    SCRAPED_DATA {
        uuid id PK
        uuid job_id FK
        jsonb raw_data
        jsonb parsed_data
        vector embedding
        timestamp scraped_at
    }
    
    JOB_LOGS {
        uuid id PK
        uuid job_id FK
        varchar level
        text message
        timestamp created_at
    }
    
    AI_ANALYSES {
        uuid id PK
        uuid data_id FK
        uuid user_id FK
        text query
        text response
        timestamp created_at
    }
```

---

## UML Class Diagram

```mermaid
classDiagram
    class User {
        +UUID id
        +String email
        +String passwordHash
        +String fullName
        +Role role
        +Boolean isActive
        +Timestamp createdAt
        +createJob() ScrapingJob
        +getJobs() List~ScrapingJob~
    }
    
    class ScrapingJob {
        +UUID id
        +UUID userId
        +String targetUrl
        +JobStatus status
        +JsonObject config
        +Timestamp scheduledAt
        +Timestamp completedAt
        +start() void
        +cancel() void
        +getData() List~ScrapedData~
    }
    
    class ScrapedData {
        +UUID id
        +UUID jobId
        +JsonObject rawData
        +JsonObject parsedData
        +float[] embedding
        +Timestamp scrapedAt
        +generateEmbedding() void
        +export(format) File
    }
    
    class AIAnalysis {
        +UUID id
        +UUID dataId
        +UUID userId
        +String query
        +String response
        +Timestamp createdAt
    }
    
    User "1" --> "*" ScrapingJob : creates
    ScrapingJob "1" --> "*" ScrapedData : produces
    ScrapedData "1" --> "*" AIAnalysis : analyzed_by
```

---

## Sequence Diagram: Data Flow

```mermaid
sequenceDiagram
    participant User
    participant API
    participant DB as PostgreSQL
    
    rect rgb(200, 220, 255)
        Note over User,DB: Create Scraping Job
        User->>API: POST /api/scrape/jobs
        API->>DB: INSERT INTO scraping_jobs
        DB-->>API: Return job_id
        API-->>User: 201 Created {id}
    end
    
    rect rgb(200, 255, 220)
        Note over User,DB: Store Scraped Data
        API->>DB: INSERT INTO scraped_data (raw_data, parsed_data)
        API->>DB: UPDATE scraping_jobs SET status='COMPLETED'
    end
    
    rect rgb(255, 220, 200)
        Note over User,DB: Query Data
        User->>API: GET /api/data?jobId=xxx
        API->>DB: SELECT * FROM scraped_data WHERE job_id=?
        DB-->>API: Return rows
        API-->>User: 200 OK [{data}]
    end
```

---

## Implementation Task List

| Order | Task | File | Test |
|-------|------|------|------|
| 1 | Create database folder structure | `database/` | - |
| 2 | Write migration V1: users table | `V1__create_users.sql` | INSERT test |
| 3 | Write migration V2: scraping_jobs | `V2__create_jobs.sql` | INSERT test |
| 4 | Write migration V3: scraped_data | `V3__create_data.sql` | INSERT test |
| 5 | Write migration V4: pgvector | `V4__enable_pgvector.sql` | Vector query |
| 6 | Write migration V5: indexes | `V5__create_indexes.sql` | Query speed |
| 7 | Create test data script | `seed_test_data.sql` | Visual check |
| 8 | Create docker-compose for PostgreSQL | `docker-compose.yml` | `docker-compose up` |

---

## File Structure

```
database/
├── migrations/
│   ├── V1__create_users.sql
│   ├── V2__create_scraping_jobs.sql
│   ├── V3__create_scraped_data.sql
│   ├── V4__enable_pgvector.sql
│   ├── V5__create_indexes.sql
│   └── V6__create_views.sql
├── seeds/
│   └── test_data.sql
├── docker-compose.yml
└── README.md
```

---

## Ready to Implement?

Once you approve this design, I will:
1. Create the `database/` folder structure
2. Write each SQL migration file
3. Set up Docker for PostgreSQL
4. Test each table with sample data
