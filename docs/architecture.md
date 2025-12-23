# System Architecture

## High-Level Architecture Diagram

```mermaid
flowchart TB
    subgraph Frontend["🖥️ Frontend (React + MVVM)"]
        UI[/"User Interface"/]
        VM["ViewModel Layer<br/>(Custom Hooks)"]
        M["Model Layer<br/>(API Services)"]
    end
    
    subgraph Backend["⚙️ Backend (Spring Boot)"]
        API["REST API Gateway"]
        SC["Scraping Controller"]
        DC["Data Controller"]
        AIC["AI Controller"]
        SVC["Services Layer"]
        REPO["Repository Layer"]
    end
    
    subgraph Scraping["🕷️ Python Scraping Service"]
        SE["Selenium Engine"]
        PW["Playwright Engine"]
        Parser["Data Parser"]
        Flask["Flask API"]
    end
    
    subgraph AI["🤖 AI Service (LangChain)"]
        LC["LangChain Core"]
        RAG["RAG Pipeline"]
        Embeddings["Vector Embeddings"]
    end
    
    subgraph Cloud["☁️ Google Cloud"]
        PG[(PostgreSQL)]
        VectorDB["pgvector Extension"]
        Storage["Cloud Storage"]
    end
    
    UI --> VM --> M
    M <-->|HTTP/REST| API
    API --> SC & DC & AIC
    SC & DC & AIC --> SVC --> REPO
    SC -->|HTTP| Flask
    Flask --> SE & PW --> Parser
    Parser -->|JSON| Flask
    AIC -->|HTTP| LC
    LC --> RAG --> Embeddings
    REPO <-->|JDBC| PG
    Embeddings --> VectorDB
    PG --> Storage
```

## Component Interaction Flow

```mermaid
sequenceDiagram
    actor User
    participant React as React Frontend
    participant Spring as Spring Boot
    participant Python as Python Scraper
    participant PG as PostgreSQL
    participant AI as LangChain AI
    
    rect rgb(173, 216, 230)
        Note over User,Python: Scraping Flow
        User->>React: Configure scraping job
        React->>Spring: POST /api/scrape/jobs
        Spring->>Python: Trigger scraping task
        Python->>Python: Execute browser automation
        Python-->>Spring: Return scraped data
        Spring->>PG: Store raw data
        Spring-->>React: Job status update
    end
    
    rect rgb(144, 238, 144)
        Note over User,AI: AI Analysis Flow
        User->>React: Request AI analysis
        React->>Spring: POST /api/ai/analyze
        Spring->>PG: Fetch relevant data
        Spring->>AI: Process with LangChain
        AI->>AI: Generate embeddings & RAG
        AI-->>Spring: Return insights
        Spring-->>React: Display results
    end
```

## MVVM Pattern (Frontend)

```mermaid
flowchart LR
    subgraph View["View Layer"]
        Components["React Components<br/>(JSX/TSX)"]
    end
    
    subgraph ViewModel["ViewModel Layer"]
        Hooks["Custom Hooks<br/>(useState, useEffect)"]
        State["State Management"]
        Logic["Business Logic"]
    end
    
    subgraph Model["Model Layer"]
        API["API Services"]
        Types["Type Definitions"]
        Transform["Data Transformers"]
    end
    
    Components -->|User Events| Hooks
    Hooks -->|State Updates| Components
    Hooks --> State --> Logic
    Logic --> API
    API --> Types --> Transform
    Transform --> Logic
```

## Data Processing Pipeline

```mermaid
flowchart LR
    A["🌐 Target Website"] -->|Scrape| B["🕷️ Python Scraper"]
    B -->|Raw HTML/JSON| C["📝 Parser"]
    C -->|Structured Data| D["⚙️ Spring Boot"]
    D -->|Store| E[("💾 PostgreSQL")]
    E -->|Query| F["🤖 LangChain"]
    F -->|Embeddings| G["📊 Vector Store"]
    G -->|Similar Search| F
    F -->|Insights| D
    D -->|Results| H["🖥️ React UI"]
```

## Technology Stack Details

### Frontend (React + TypeScript)
- **Framework**: React 18+
- **Language**: TypeScript
- **State**: React Query + Context
- **Styling**: Tailwind CSS / CSS Modules
- **HTTP Client**: Axios

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: Spring Data JPA
- **Security**: Spring Security + JWT
- **Docs**: OpenAPI 3.0

### Python Scraper
- **Framework**: Flask
- **Scraping**: Selenium, Playwright
- **Parsing**: BeautifulSoup, lxml
- **Async**: asyncio, aiohttp

### AI Service (LangChain)
- **Framework**: LangChain4j (Java)
- **LLM**: OpenAI / Local models
- **Vector DB**: pgvector
- **Embeddings**: OpenAI / Sentence Transformers
