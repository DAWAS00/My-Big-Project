# API Reference

## Base URL
```
Production: https://api.webscraper.example.com/v1
Development: http://localhost:8080/api/v1
```

## Authentication
All endpoints require JWT authentication:
```
Authorization: Bearer <your-jwt-token>
```

---

## Scraping Endpoints

### Create Scraping Job
```http
POST /scrape/jobs
```

**Request Body:**
```json
{
  "targetUrl": "https://example.com",
  "config": {
    "engine": "playwright",
    "waitForSelector": ".content",
    "extractRules": [
      { "name": "title", "selector": "h1", "type": "text" },
      { "name": "links", "selector": "a", "type": "attribute", "attr": "href" }
    ],
    "pagination": {
      "enabled": true,
      "nextSelector": ".next-page",
      "maxPages": 10
    }
  },
  "schedule": "0 0 * * *"
}
```

**Response:** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "targetUrl": "https://example.com",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### Get Job Status
```http
GET /scrape/jobs/{jobId}
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "targetUrl": "https://example.com",
  "dataCount": 150,
  "startedAt": "2024-01-15T10:30:05Z",
  "completedAt": "2024-01-15T10:32:15Z"
}
```

### List Jobs
```http
GET /scrape/jobs?page=0&size=20&status=COMPLETED
```

### Cancel Job
```http
DELETE /scrape/jobs/{jobId}
```

---

## Data Endpoints

### Query Scraped Data
```http
GET /data?jobId={jobId}&page=0&size=50
```

### Get Data by ID
```http
GET /data/{dataId}
```

### Export Data
```http
GET /data/export?jobId={jobId}&format=csv
```
Supported formats: `csv`, `json`, `excel`

---

## AI Endpoints

### Analyze Data
```http
POST /ai/analyze
```

**Request Body:**
```json
{
  "query": "Summarize the main products and their prices",
  "dataIds": ["id1", "id2"],
  "analysisType": "SUMMARY"
}
```

**Response:** `200 OK`
```json
{
  "id": "analysis-id",
  "response": "Based on the scraped data, there are 50 products...",
  "tokensUsed": 1250,
  "createdAt": "2024-01-15T11:00:00Z"
}
```

### Semantic Search
```http
POST /ai/search
```

**Request Body:**
```json
{
  "query": "products under $50",
  "jobId": "job-id",
  "limit": 10
}
```

---

## Error Responses

```json
{
  "error": "ValidationError",
  "message": "Invalid URL format",
  "details": { "field": "targetUrl" },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

| Status | Description |
|--------|-------------|
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing/invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource doesn't exist |
| 429 | Too Many Requests - Rate limited |
| 500 | Internal Server Error |
