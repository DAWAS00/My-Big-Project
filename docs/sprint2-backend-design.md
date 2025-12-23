# Sprint 2: Spring Boot Backend Design

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 3.2.x |
| Language | Java | 21 (LTS) |
| Build Tool | Maven | 3.9.x |
| ORM | Spring Data JPA + Hibernate | - |
| Database | PostgreSQL | 16 |
| Vector | pgvector (via native SQL) | 0.8.x |
| Security | Spring Security + JWT | - |
| Docs | SpringDoc OpenAPI | 2.x |
| Export | Apache POI (Excel) | 5.x |

---

## Project Structure

```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/webscraper/
│   │   │   ├── WebScraperApplication.java
│   │   │   │
│   │   │   ├── config/                    # Configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   │
│   │   │   ├── controller/                # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── TargetController.java
│   │   │   │   ├── JobController.java
│   │   │   │   ├── DataController.java
│   │   │   │   └── AIController.java
│   │   │   │
│   │   │   ├── service/                   # Business Logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── TargetService.java
│   │   │   │   ├── JobService.java
│   │   │   │   ├── ScrapingService.java
│   │   │   │   ├── DataService.java
│   │   │   │   ├── ExportService.java
│   │   │   │   └── AIService.java
│   │   │   │
│   │   │   ├── repository/                # Data Access
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TargetRepository.java
│   │   │   │   ├── JobRepository.java
│   │   │   │   ├── PageRepository.java
│   │   │   │   ├── PageVersionRepository.java
│   │   │   │   ├── ChunkRepository.java
│   │   │   │   ├── EmbeddingRepository.java
│   │   │   │   └── AIRepository.java
│   │   │   │
│   │   │   ├── entity/                    # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Target.java
│   │   │   │   ├── ScrapeJob.java
│   │   │   │   ├── JobLog.java
│   │   │   │   ├── Page.java
│   │   │   │   ├── PageVersion.java
│   │   │   │   ├── Chunk.java
│   │   │   │   ├── Embedding.java
│   │   │   │   ├── AIRequest.java
│   │   │   │   ├── AIResponse.java
│   │   │   │   └── Citation.java
│   │   │   │
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateTargetRequest.java
│   │   │   │   │   ├── CreateJobRequest.java
│   │   │   │   │   └── AIQueryRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── TargetResponse.java
│   │   │   │       ├── JobResponse.java
│   │   │   │       ├── PageDataResponse.java
│   │   │   │       └── AIResponse.java
│   │   │   │
│   │   │   ├── exception/                 # Error Handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── ValidationException.java
│   │   │   │
│   │   │   └── util/                      # Utilities
│   │   │       ├── JwtUtil.java
│   │   │       └── HashUtil.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   │
│   └── test/
│       └── java/com/webscraper/
│           ├── controller/
│           └── service/
│
└── Dockerfile
```

---

## Layer Responsibilities

### Controller Layer
- Handle HTTP requests/responses
- Input validation with `@Valid`
- No business logic
- Return `ResponseEntity<T>`

### Service Layer
- Business logic
- Transaction management (`@Transactional`)
- Call external services (Python scraper)
- Data transformation (Entity ↔ DTO)

### Repository Layer
- Data access via Spring Data JPA
- Custom queries with `@Query`
- Native SQL for vector operations

---

## API Endpoints Design

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login, get JWT |

### Targets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/targets` | Create target |
| GET | `/api/targets` | List user's targets |
| GET | `/api/targets/{id}` | Get target details |
| PUT | `/api/targets/{id}` | Update target |
| DELETE | `/api/targets/{id}` | Delete target |

### Jobs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/jobs` | Create scraping job |
| GET | `/api/jobs` | List jobs |
| GET | `/api/jobs/{id}` | Get job status |
| DELETE | `/api/jobs/{id}` | Cancel job |
| GET | `/api/jobs/{id}/logs` | Get job logs |

### Data
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/data/pages` | List pages |
| GET | `/api/data/pages/{id}/versions` | Page version history |
| GET | `/api/data/export` | Export to Excel/CSV |

### AI
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/query` | Ask question |
| GET | `/api/ai/history` | Query history |

---

## Entity-to-Table Mapping

| Entity | Table | Key Fields |
|--------|-------|------------|
| User | users | id, email, role |
| Target | targets | id, userId, baseUrl, scrapeConfig |
| ScrapeJob | scrape_jobs | id, targetId, status |
| JobLog | job_logs | id, jobId, level, message |
| Page | pages | id, targetId, urlHash |
| PageVersion | page_versions | id, pageId, contentHash |
| Chunk | chunks | id, pageVersionId, content |
| Embedding | embeddings | id, chunkId, embedding |
| AIRequest | ai_requests | id, userId, query |
| AIResponse | ai_responses | id, requestId, response |
| Citation | citations | id, responseId, chunkId |

---

## Key Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Core -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    
    <!-- Excel Export -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    
    <!-- OpenAPI Docs -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## Implementation Order

| Step | Task | Files |
|------|------|-------|
| 1 | Initialize project | pom.xml, application.yml |
| 2 | Create entities | entity/*.java |
| 3 | Create repositories | repository/*.java |
| 4 | Create DTOs | dto/**/*.java |
| 5 | Create services | service/*.java |
| 6 | Create controllers | controller/*.java |
| 7 | Add security | config/SecurityConfig.java |
| 8 | Add export | service/ExportService.java |
| 9 | Add tests | test/**/*.java |

---

## Next Step

**Approve this design?** I'll then:
1. Create the Spring Boot project with Maven
2. Set up the entity classes matching our database
3. Build the REST controllers
