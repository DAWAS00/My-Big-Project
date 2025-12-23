# Web Scraping & AI Data Processing Platform

A comprehensive platform for scraping, processing, and analyzing website data using AI-powered insights.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                             │
│                         MVVM Architecture                            │
├─────────────────────────────────────────────────────────────────────┤
│                              ↕ REST API                              │
├─────────────────────────────────────────────────────────────────────┤
│                        BACKEND (Spring Boot)                         │
│                    Controllers → Services → Repos                    │
├───────────────────┬─────────────────────────────────────────────────┤
│   Python Scraper  │              LangChain AI                        │
│ Selenium/Playwright│            RAG Pipeline                         │
├───────────────────┴─────────────────────────────────────────────────┤
│                     PostgreSQL (Google Cloud)                        │
└─────────────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
web-scraper-platform/
├── frontend/                 # React MVVM Application
├── backend/                  # Spring Boot REST API
├── scraper/                  # Python Scraping Service
├── ai-service/               # LangChain AI Integration
├── database/                 # SQL Migrations & Scripts
└── docs/                     # Documentation
```

## 🚀 Quick Start

See individual component READMEs for setup instructions:
- [Frontend Setup](./docs/frontend-guide.md)
- [Backend Setup](./docs/backend-guide.md)
- [Scraper Setup](./docs/scraper-guide.md)
- [AI Service Setup](./docs/ai-guide.md)

## 📚 Documentation

- [System Architecture](./docs/architecture.md)
- [API Reference](./docs/api-reference.md)
- [Database Schema](./docs/database-schema.md)
- [Deployment Guide](./docs/deployment.md)

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Frontend | React, TypeScript, MVVM |
| Backend | Spring Boot, Java 17 |
| Scraper | Python, Selenium, Playwright |
| AI | LangChain, Java bindings |
| Database | PostgreSQL, pgvector |
| Cloud | Google Cloud Platform |
