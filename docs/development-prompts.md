# Development Prompts & Best Practices

## Prompts for Each Component

### 1. React Frontend (MVVM)
```
Create a React component for [FEATURE_NAME] following MVVM pattern:

VIEW LAYER:
- Create functional component with TypeScript
- Use props for display data only
- No business logic in JSX

VIEWMODEL LAYER (Custom Hook):
- Create useFeatureName hook
- Handle all state management
- Include loading/error states
- Implement event handlers

MODEL LAYER:
- Create API service functions
- Define TypeScript interfaces
- Add data transformation utilities

REQUIREMENTS:
- Follow Single Responsibility Principle
- Use React Query for server state
- Implement proper error boundaries
- Add accessibility attributes
```

### 2. Spring Boot Backend
```
Create a Spring Boot REST endpoint for [ENDPOINT_NAME]:

CONTROLLER:
- Use @RestController with proper mapping
- Implement input validation with @Valid
- Return ResponseEntity with appropriate status

SERVICE:
- @Transactional where needed
- Business logic only (no HTTP concerns)
- Use constructor injection

REPOSITORY:
- Spring Data JPA interface
- Custom queries with @Query if needed
- Pagination support

REQUIREMENTS:
- Follow Open/Closed Principle
- Use DTOs for API contracts
- Implement proper exception handling
- Add unit tests with MockMvc
- O(1) or O(log n) time complexity preferred
```

### 3. Python Scraper
```
Create a Python scraping function for [TARGET_TYPE]:

IMPLEMENTATION:
- Use Playwright for dynamic content
- Use Selenium as fallback
- Implement retry logic with exponential backoff

DATA EXTRACTION:
- Define selector configuration
- Handle missing elements gracefully  
- Validate extracted data

REQUIREMENTS:
- Async where possible
- Respect robots.txt
- Rate limiting (1 req/second)
- Memory efficient (stream large data)
- O(n) time complexity maximum
```

### 4. LangChain AI Service
```
Create an AI analysis function for [ANALYSIS_TYPE]:

RAG PIPELINE:
- Chunk data appropriately (512 tokens)
- Generate and store embeddings
- Implement similarity search

PROMPT ENGINEERING:
- Clear system instructions
- Include relevant context only
- Set appropriate temperature

REQUIREMENTS:
- Lazy loading for embeddings
- Cache frequent queries
- Token usage optimization
- Handle API rate limits
```

---

## SOLID Principles Checklist

### Single Responsibility (S)
- [ ] Each class/function has ONE job
- [ ] Services don't mix concerns

### Open/Closed (O)
- [ ] Use interfaces for extensibility
- [ ] Strategy pattern for algorithms

### Liskov Substitution (L)
- [ ] Child classes work as parents
- [ ] Consistent contracts

### Interface Segregation (I)
- [ ] Small, focused interfaces
- [ ] No unused dependencies

### Dependency Inversion (D)
- [ ] Depend on abstractions
- [ ] Constructor injection

---

## Time Complexity Guidelines

| Operation | Target | Max Allowed |
|-----------|--------|-------------|
| DB lookup by ID | O(1) | O(1) |
| List with pagination | O(n) | O(n) |
| Search with index | O(log n) | O(log n) |
| Batch processing | O(n) | O(n log n) |
| Sorting | O(n log n) | O(n log n) |

**Avoid:**
- Nested loops O(n²) 
- Recursive without memo
- N+1 query problems

---

## Testing Strategy

### Before Implementation
1. Write test cases first (TDD)
2. Define expected inputs/outputs
3. Identify edge cases

### Unit Tests
```bash
# React
npm test -- --coverage

# Spring Boot
./mvnw test

# Python
pytest --cov=app
```

### Integration Tests
```bash
# API tests
./mvnw test -Pintegration

# E2E tests
npx playwright test
```

---

## Code Review Checklist

- [ ] Follows naming conventions
- [ ] No magic numbers/strings
- [ ] Error handling complete
- [ ] Logging for debugging
- [ ] No commented-out code
- [ ] Tests passing
- [ ] Documentation updated
