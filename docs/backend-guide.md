# Spring Boot Backend Guide

## Project Structure

```
backend/
├── src/main/java/com/webscraper/
│   ├── WebScraperApplication.java
│   │
│   ├── controller/
│   │   ├── ScrapingController.java
│   │   ├── DataController.java
│   │   └── AIController.java
│   │
│   ├── service/
│   │   ├── ScrapingService.java
│   │   ├── DataService.java
│   │   ├── AIService.java
│   │   └── PythonScraperClient.java
│   │
│   ├── repository/
│   │   ├── ScrapingJobRepository.java
│   │   ├── ScrapedDataRepository.java
│   │   └── AIAnalysisRepository.java
│   │
│   ├── model/
│   │   ├── entity/
│   │   │   ├── ScrapingJob.java
│   │   │   ├── ScrapedData.java
│   │   │   └── User.java
│   │   └── dto/
│   │       ├── CreateJobRequest.java
│   │       ├── JobResponse.java
│   │       └── AnalyzeRequest.java
│   │
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── WebClientConfig.java
│   │   └── OpenApiConfig.java
│   │
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
│
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       └── V1__init.sql
│
└── pom.xml
```

## Key Components

### Controller Layer
```java
// controller/ScrapingController.java
@RestController
@RequestMapping("/api/v1/scrape")
@RequiredArgsConstructor
public class ScrapingController {
    
    private final ScrapingService scrapingService;
    
    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal User user) {
        
        ScrapingJob job = scrapingService.createJob(request, user);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(JobResponse.from(job));
    }
    
    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID id) {
        return scrapingService.findById(id)
            .map(JobResponse::from)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }
    
    @GetMapping("/jobs")
    public ResponseEntity<Page<JobResponse>> listJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ScrapingJob> jobs = scrapingService.findAll(status, pageable);
        return ResponseEntity.ok(jobs.map(JobResponse::from));
    }
    
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> cancelJob(@PathVariable UUID id) {
        scrapingService.cancelJob(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Service Layer
```java
// service/ScrapingService.java
@Service
@RequiredArgsConstructor
@Transactional
public class ScrapingService {
    
    private final ScrapingJobRepository jobRepository;
    private final PythonScraperClient scraperClient;
    private final ApplicationEventPublisher eventPublisher;
    
    public ScrapingJob createJob(CreateJobRequest request, User user) {
        ScrapingJob job = ScrapingJob.builder()
            .userId(user.getId())
            .targetUrl(request.getTargetUrl())
            .config(request.getConfig())
            .status(JobStatus.PENDING)
            .build();
        
        job = jobRepository.save(job);
        
        // Trigger async scraping
        eventPublisher.publishEvent(new JobCreatedEvent(job.getId()));
        
        return job;
    }
    
    @Async
    @EventListener
    public void handleJobCreated(JobCreatedEvent event) {
        ScrapingJob job = jobRepository.findById(event.getJobId())
            .orElseThrow();
        
        try {
            job.setStatus(JobStatus.RUNNING);
            job.setStartedAt(Instant.now());
            jobRepository.save(job);
            
            // Call Python scraper
            ScrapingResult result = scraperClient.scrape(job);
            
            // Save scraped data
            // ...
            
            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(Instant.now());
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setRetryCount(job.getRetryCount() + 1);
        }
        
        jobRepository.save(job);
    }
}
```

### Python Scraper Client
```java
// service/PythonScraperClient.java
@Service
@RequiredArgsConstructor
public class PythonScraperClient {
    
    private final WebClient webClient;
    
    @Value("${scraper.service.url}")
    private String scraperServiceUrl;
    
    public ScrapingResult scrape(ScrapingJob job) {
        return webClient.post()
            .uri(scraperServiceUrl + "/scrape")
            .bodyValue(Map.of(
                "url", job.getTargetUrl(),
                "config", job.getConfig()
            ))
            .retrieve()
            .bodyToMono(ScrapingResult.class)
            .timeout(Duration.ofMinutes(5))
            .block();
    }
}
```

### Entity
```java
// model/entity/ScrapingJob.java
@Entity
@Table(name = "scraping_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapingJob {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "target_url", nullable = false)
    private String targetUrl;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> config;
    
    private String scheduleCron;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant completedAt;
    private Integer retryCount = 0;
    
    @CreationTimestamp
    private Instant createdAt;
}
```

## Configuration

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:5432/webscraper
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:password}
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  flyway:
    enabled: true
    locations: classpath:db/migration

scraper:
  service:
    url: http://localhost:5000

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
```

## Setup Commands

```bash
# Generate project with Spring Initializr
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,postgresql,flyway,security,validation,lombok \
  -d type=maven-project \
  -d language=java \
  -d javaVersion=17 \
  -d name=webscraper \
  -o backend.zip

# Or use Spring Initializr web UI
```

## Key Dependencies (pom.xml)
```xml
<dependencies>
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
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.vladmihalcea</groupId>
        <artifactId>hibernate-types-60</artifactId>
        <version>2.21.1</version>
    </dependency>
</dependencies>
```
