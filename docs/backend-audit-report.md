# Backend Audit Report

## Current State vs Clean Architecture Design

### Structure Comparison

| Design (Clean Architecture) | Current Code | Status | Issue |
|-----------------------------|--------------|--------|-------|
| `domain/entity/` | `entity/` | ⚠️ Partial | JPA annotations in domain entities |
| `domain/valueobject/` | (missing) | ❌ Missing | No value objects |
| `domain/service/` | (missing) | ❌ Missing | No domain services |
| `application/usecase/` | `service/` | ⚠️ Wrong | Services not use-case pattern |
| `application/port/out/` | `repository/` | ⚠️ Wrong | Repos in wrong layer |
| `infrastructure/persistence/` | (missing) | ❌ Missing | No adapter layer |
| `infrastructure/external/` | (missing) | ❌ Missing | No scraper/AI gateways |
| `web/controller/` | (missing) | ❌ Missing | No controllers yet |
| `web/request/` | `dto/request/` | ⚠️ Rename | Should be under `web/` |
| `web/response/` | `dto/response/` | ⚠️ Rename | Should be under `web/` |

---

## Violations Found

### 1. Domain Layer Issues

**Problem**: Entities have JPA annotations (infrastructure concern)

```java
// Current: User.java has @Entity, @Table, @Column
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
```

**Clean Architecture says**: Domain entities should be pure Java, no framework annotations.

**Fix**: Create separate JPA entities in `infrastructure/persistence/jpa/entity/`

---

### 2. Application Layer Issues

**Problem**: Services mix business logic + repository calls directly

```java
// Current: TargetService directly uses Spring Data
@Service
public class TargetService {
    private final TargetRepository targetRepository; // Should use Port interface
```

**Clean Architecture says**: Use-cases depend on Port interfaces, not Spring Data

**Fix**: Create `application/port/out/TargetRepository` interface

---

### 3. Missing Components

| Component | Purpose | Priority |
|-----------|---------|----------|
| Use-Cases | Single-purpose operations | High |
| Ports (Interfaces) | Dependency inversion | High |
| Adapters | Implement ports | High |
| Controllers | REST endpoints | High |
| Value Objects | ContentHash, UrlHash | Medium |
| Domain Services | Hash computation | Medium |

---

## Database Alignment ✅

Database migrations match the design:

| Table | Migration | Entity | Status |
|-------|-----------|--------|--------|
| users | V2 | User.java | ✅ Match |
| targets | V3 | Target.java | ✅ Match |
| scrape_jobs | V4 | ScrapeJob.java | ✅ Match |
| job_logs | V5 | JobLog.java | ✅ Match |
| pages | V6 | Page.java | ✅ Match |
| page_versions | V7 | PageVersion.java | ✅ Match |
| chunks | V8 | Chunk.java | ✅ Match |
| embeddings | V9 | (no entity) | ⚠️ Missing |
| ai_requests | V10 | AIRequest.java | ✅ Match |
| ai_responses | V10 | AIResponse.java | ✅ Match |
| citations | V10 | Citation.java | ✅ Match |

---

## Clean Code Violations

| Rule | Current Code | Violation |
|------|--------------|-----------|
| SRP | Services do CRUD + validation | Multiple responsibilities |
| DIP | Services depend on concrete repos | Should depend on interfaces |
| Domain independence | Entities have JPA | Domain depends on infrastructure |
| No controllers | Missing | Can't test API |
| No logging | Missing | No structured logs |

---

## Recommended Fix Order

1. ✅ **Keep database** - migrations are correct
2. 🔧 **Add missing `Embedding.java` entity**
3. 🔧 **Create Port interfaces** in `application/port/out/`
4. 🔧 **Move JPA entities** to `infrastructure/persistence/jpa/entity/`
5. 🔧 **Create Adapters** implementing ports
6. 🔧 **Refactor Services → Use-Cases**
7. 🔧 **Create Controllers** in `web/controller/`
8. 🔧 **Add Security Config**

---

## Decision Required

**Option A**: Full restructure now (2-3 hours)
- Follow Clean Architecture 100%
- More maintainable long-term
- More files to manage

**Option B**: Pragmatic approach (1 hour)
- Keep current structure
- Add interfaces for critical paths only (JobQueue, ScraperGateway)
- Add controllers to make API work
- Restructure later

**Which approach do you prefer?**
