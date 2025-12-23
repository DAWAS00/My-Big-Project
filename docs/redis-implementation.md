# Redis Caching Implementation

## What is Redis?

Redis is an **in-memory data store** used for:
- **Caching**: Store frequently accessed data to reduce DB load
- **Session storage**: User sessions without DB queries
- **Rate limiting**: Prevent API abuse
- **Job queue**: Alternative to RabbitMQ (simpler)

---

## Why Redis for Your Project?

| Use Case | Without Redis | With Redis |
|----------|---------------|------------|
| Get same page 100 times | 100 DB queries | 1 DB query + 99 cache hits |
| User session check | DB query each request | In-memory lookup |
| Rate limit API | Complex DB logic | Simple counter |
| Scraped data lookup | Slow for large data | Instant retrieval |

### Expected Performance Improvement
- **Page lookup**: 50ms → 1ms (50x faster)
- **User auth check**: 10ms → 0.5ms (20x faster)
- **DB load**: Reduced by 60-80%

---

## What We'll Build

### 1. Cache Port (Interface)
```java
public interface CacheService {
    void put(String key, Object value, Duration ttl);
    Optional<Object> get(String key);
    void evict(String key);
    void evictByPattern(String pattern);
}
```

### 2. Redis Implementation
```java
@Component
public class RedisCacheService implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    // ...
}
```

### 3. Caching Decorator Pattern
```java
// Wraps repository, checks cache first
@Component
@Primary
public class CachingPageRepository implements PageRepository {
    private final PageRepository delegate;
    private final CacheService cache;
    
    public Optional<Page> findById(UUID id) {
        String key = "page:" + id;
        return cache.get(key)
            .or(() -> {
                var page = delegate.findById(id);
                page.ifPresent(p -> cache.put(key, p, Duration.ofHours(1)));
                return page;
            });
    }
}
```

---

## Cache Strategy

| Data Type | TTL | Cache Key Pattern | Eviction |
|-----------|-----|-------------------|----------|
| Page | 1 hour | `page:{id}` | On update |
| PageVersion | 24 hours | `pageversion:{id}` | Never (immutable) |
| User | 15 minutes | `user:{id}` | On update |
| Target | 30 minutes | `target:{id}` | On update |
| AI Response | 1 hour | `ai:{queryHash}` | Manual |

---

## Docker Setup

Add to `docker-compose.yml`:
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
  volumes:
    - redis_data:/data
```

---

## Implementation Steps

1. Add Redis dependency to pom.xml
2. Create CacheService port interface
3. Create RedisCacheService implementation
4. Update docker-compose.yml with Redis
5. Add caching decorators for hot paths
6. Update application.yml with Redis config

---

## Files to Create/Modify

| File | Action |
|------|--------|
| `pom.xml` | Add spring-data-redis |
| `application/port/out/CacheService.java` | NEW - Cache interface |
| `infrastructure/cache/RedisCacheService.java` | NEW - Redis impl |
| `infrastructure/config/RedisConfig.java` | NEW - Redis config |
| `database/docker-compose.yml` | ADD - Redis service |
| `application.yml` | ADD - Redis connection |

**Proceed with implementation?**
