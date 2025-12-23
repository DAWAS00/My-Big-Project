# Clean Architecture Backend Design

## A) Module Structure

```
backend/src/main/java/com/webscraper/
├── domain/                    # CORE: Entities + Business Rules (no dependencies)
│   ├── entity/
│   │   ├── User.java
│   │   ├── Target.java
│   │   ├── ScrapeJob.java
│   │   ├── Page.java
│   │   ├── PageVersion.java
│   │   ├── Chunk.java
│   │   └── Citation.java
│   ├── valueobject/
│   │   ├── ContentHash.java
│   │   ├── UrlHash.java
│   │   └── JobStatus.java
│   ├── exception/
│   │   ├── DomainException.java
│   │   └── EntityNotFoundException.java
│   └── service/               # Domain services (pure business logic)
│       └── ContentHasher.java
│
├── application/               # USE-CASES: Orchestrate domain, define ports
│   ├── usecase/
│   │   ├── target/
│   │   │   ├── CreateTargetUseCase.java
│   │   │   ├── GetTargetsUseCase.java
│   │   │   └── UpdateTargetUseCase.java
│   │   ├── job/
│   │   │   ├── CreateJobUseCase.java
│   │   │   ├── UpdateJobStatusUseCase.java
│   │   │   └── CancelJobUseCase.java
│   │   ├── scraping/
│   │   │   ├── SavePageVersionUseCase.java
│   │   │   └── DescribePageUseCase.java
│   │   ├── rag/
│   │   │   ├── ChunkContentUseCase.java
│   │   │   ├── EmbedChunksUseCase.java
│   │   │   └── AskAIUseCase.java
│   │   └── auth/
│   │       ├── RegisterUserUseCase.java
│   │       └── LoginUserUseCase.java
│   ├── port/                  # Interfaces (dependency inversion)
│   │   ├── in/                # Primary ports (use-cases)
│   │   │   └── (use-case interfaces)
│   │   └── out/               # Secondary ports (driven)
│   │       ├── UserRepository.java
│   │       ├── TargetRepository.java
│   │       ├── JobRepository.java
│   │       ├── PageRepository.java
│   │       ├── ChunkRepository.java
│   │       ├── JobQueue.java
│   │       ├── ScraperGateway.java
│   │       ├── EmbeddingService.java
│   │       └── LLMService.java
│   └── dto/                   # Command/Query objects
│       ├── command/
│       │   ├── CreateTargetCommand.java
│       │   ├── CreateJobCommand.java
│       │   └── AskAICommand.java
│       └── result/
│           ├── TargetResult.java
│           ├── JobResult.java
│           └── AIResult.java
│
├── infrastructure/            # ADAPTERS: DB, HTTP, Queue implementations
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── entity/        # JPA-specific entities with annotations
│   │   │   ├── repository/    # Spring Data JPA implementations
│   │   │   └── mapper/        # Entity <-> Domain mappers
│   │   └── adapter/
│   │       ├── UserRepositoryAdapter.java
│   │       ├── JobRepositoryAdapter.java
│   │       └── PageRepositoryAdapter.java
│   ├── external/
│   │   ├── scraper/
│   │   │   └── HttpScraperGateway.java   # Calls Python service
│   │   ├── ai/
│   │   │   ├── OpenAIEmbeddingService.java
│   │   │   └── OpenAILLMService.java
│   │   └── queue/
│   │       ├── InMemoryJobQueue.java     # Dev
│   │       └── RabbitJobQueue.java       # Prod (swappable)
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java
│   │   └── QueueConfig.java
│   └── security/
│       ├── JwtTokenProvider.java
│       └── JwtAuthFilter.java
│
└── web/                       # INTERFACE: Controllers + Adapters
    ├── controller/
    │   ├── AuthController.java
    │   ├── TargetController.java
    │   ├── JobController.java
    │   ├── DataController.java
    │   └── AIController.java
    ├── request/               # HTTP Request DTOs
    │   ├── RegisterRequest.java
    │   ├── CreateTargetRequest.java
    │   └── AIQueryRequest.java
    ├── response/              # HTTP Response DTOs
    │   ├── ApiResponse.java
    │   ├── TargetResponse.java
    │   └── JobResponse.java
    ├── mapper/                # Request -> Command, Result -> Response
    │   ├── TargetWebMapper.java
    │   └── JobWebMapper.java
    └── exception/
        └── GlobalExceptionHandler.java
```

### Folder Responsibilities

| Folder | Responsibility | Dependencies |
|--------|---------------|--------------|
| `domain/` | Pure business entities + rules | **NONE** |
| `application/` | Use-cases, ports (interfaces) | domain |
| `infrastructure/` | DB, HTTP, queue implementations | application, domain |
| `web/` | REST controllers, HTTP adapters | application |

---

## B) Core Use-Cases

| Use-Case | Input | Output | Description |
|----------|-------|--------|-------------|
| `CreateTargetUseCase` | CreateTargetCommand | TargetResult | Create new scraping target |
| `RunScrapeJobUseCase` | CreateJobCommand | JobResult | Queue a scraping job |
| `UpdateJobStatusUseCase` | JobId, Status | void | Worker updates job progress |
| `SavePageVersionUseCase` | PageData, ContentHash | PageVersionId | Store scraped page (dedup) |
| `ChunkContentUseCase` | PageVersionId | List<ChunkId> | Split content into chunks |
| `EmbedChunksUseCase` | List<ChunkId> | void | Generate vector embeddings |
| `AskAIUseCase` | AskAICommand | AIResult | RAG query with citations |
| `DescribePageUseCase` | PageId | PageSummary | Get page metadata + versions |

### Use-Case Signatures

```java
// CreateTargetUseCase.java
interface CreateTargetUseCase {
    TargetResult execute(CreateTargetCommand cmd, UUID userId);
}

// SavePageVersionUseCase.java
interface SavePageVersionUseCase {
    Optional<UUID> execute(UUID pageId, String rawHtml, ContentHash hash);
    // Returns empty if hash matches (no change)
}

// AskAIUseCase.java
interface AskAIUseCase {
    AIResult execute(AskAICommand cmd);
    // AIResult contains response + List<Citation>
}
```

---

## C) Interfaces + Implementations

### JobQueue (Swappable)
```java
// Port
interface JobQueue {
    void enqueue(ScrapeJob job);
    Optional<ScrapeJob> dequeue();
    void acknowledge(UUID jobId);
}

// Implementations (swap via config)
class InMemoryJobQueue implements JobQueue { }    // Dev
class RabbitJobQueue implements JobQueue { }      // Prod
class RedisJobQueue implements JobQueue { }       // Alt
```

### ScraperGateway (Calls Python)
```java
// Port
interface ScraperGateway {
    ScrapingResult scrape(ScrapeRequest request);
}

// Implementation
@Component
class HttpScraperGateway implements ScraperGateway {
    private final WebClient webClient;
    
    ScrapingResult scrape(ScrapeRequest req) {
        return webClient.post()
            .uri(scraperUrl + "/scrape")
            .bodyValue(req)
            .retrieve()
            .bodyToMono(ScrapingResult.class)
            .block();
    }
}
```

### PageRepository (Port)
```java
// Port (in application layer)
interface PageRepository {
    Optional<Page> findByUrlHash(UrlHash hash);
    Page save(Page page);
    List<Page> findByTargetId(UUID targetId, Pageable pageable);
}

// Adapter (in infrastructure)
@Component
class PageRepositoryAdapter implements PageRepository {
    private final JpaPageRepository jpaRepo;
    private final PageMapper mapper;
    
    Optional<Page> findByUrlHash(UrlHash hash) {
        return jpaRepo.findByUrlHash(hash.value())
            .map(mapper::toDomain);
    }
}
```

---

## D) REST API Design

### Endpoints

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| POST | `/api/auth/register` | `{email, password, fullName}` | `{token, user}` |
| POST | `/api/auth/login` | `{email, password}` | `{token, user}` |
| POST | `/api/targets` | `{name, baseUrl, config}` | `{id, name, ...}` |
| GET | `/api/targets` | `?page=0&size=20` | `{content: [...], page}` |
| GET | `/api/targets/{id}` | - | `{id, name, ...}` |
| POST | `/api/jobs` | `{targetId, config}` | `{id, status}` |
| GET | `/api/jobs` | `?status=PENDING` | `{content: [...]}` |
| DELETE | `/api/jobs/{id}` | - | `{id, status: CANCELLED}` |
| GET | `/api/data/pages?targetId=` | - | `{content: [...]}` |
| GET | `/api/data/export?targetId=&format=excel` | - | File download |
| POST | `/api/ai/query` | `{query, targetId?}` | `{response, citations}` |

### Standard Response Envelope
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Email already exists",
    "details": { "field": "email" }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## E) Anti-Duplication Plan

### Identified Repeated Logic

| Pattern | Location | Extract To |
|---------|----------|------------|
| User ownership check | All services | `OwnershipValidator.validate(userId, entity)` |
| Pagination response | All list endpoints | `PagedResponse<T>.from(Page<T>)` |
| Entity-to-DTO mapping | Controllers | `*WebMapper` classes |
| Domain-to-JPA mapping | Repositories | `*PersistenceMapper` classes |
| Hash computation | Page, Version | `ContentHasher.hash(content)` |
| JWT token logic | Auth, Filter | `JwtTokenProvider` |
| Error response building | Exception handlers | `ApiResponse.error(code, msg)` |

### Extraction Examples

```java
// OwnershipValidator.java
@Component
class OwnershipValidator {
    void validate(UUID userId, OwnedEntity entity) {
        if (!entity.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Not owner");
        }
    }
}

// ApiResponse.java (factory methods)
record ApiResponse<T>(boolean success, T data, ErrorInfo error, Instant timestamp) {
    static <T> ApiResponse<T> ok(T data) { ... }
    static <T> ApiResponse<T> error(String code, String msg) { ... }
}
```

---

## F) Definition of Done Checklist

### Code Quality
- [ ] No class > 200 lines
- [ ] No method > 20 lines
- [ ] No duplicate logic (DRY)
- [ ] Each class has single responsibility
- [ ] Interfaces for external dependencies
- [ ] Domain layer has zero infrastructure imports

### Testing
- [ ] Unit tests for each use-case (≥80% coverage)
- [ ] Integration tests for repositories
- [ ] Controller tests with MockMvc
- [ ] External services mocked

### Error Handling
- [ ] Typed domain exceptions
- [ ] Global exception handler
- [ ] Consistent error JSON structure
- [ ] No stack traces in responses

### Configuration
- [ ] No hardcoded values
- [ ] Secrets via environment variables
- [ ] Separate dev/prod configs

### Documentation
- [ ] README with setup instructions
- [ ] OpenAPI docs auto-generated
- [ ] Each use-case has Javadoc

### Performance
- [ ] Indexes on hot query paths
- [ ] Pagination on all list endpoints
- [ ] Lazy loading for relations

---

## Migration Plan

1. **Keep existing code working** (current structure runs)
2. **Create new packages** (domain/, application/)
3. **Extract domain entities** (remove JPA from domain)
4. **Create ports** (repository interfaces)
5. **Move JPA to infrastructure** (adapters)
6. **Refactor services → use-cases**
7. **Update controllers** (thin, validate + call use-case)
8. **Add tests**
