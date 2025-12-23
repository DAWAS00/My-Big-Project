# React Frontend Guide (MVVM Architecture)

## Project Structure

```
frontend/
├── public/
├── src/
│   ├── views/                    # View Layer (UI Components)
│   │   ├── Dashboard/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Dashboard.module.css
│   │   │   └── index.ts
│   │   ├── ScrapingConfig/
│   │   │   ├── ScrapingConfig.tsx
│   │   │   ├── components/
│   │   │   │   ├── UrlInput.tsx
│   │   │   │   ├── SelectorBuilder.tsx
│   │   │   │   └── SchedulePicker.tsx
│   │   │   └── index.ts
│   │   ├── DataViewer/
│   │   │   ├── DataViewer.tsx
│   │   │   ├── DataTable.tsx
│   │   │   └── index.ts
│   │   └── AIAnalysis/
│   │       ├── AIAnalysis.tsx
│   │       ├── QueryInput.tsx
│   │       ├── ResultsPanel.tsx
│   │       └── index.ts
│   │
│   ├── viewmodels/               # ViewModel Layer (Hooks)
│   │   ├── useDashboard.ts
│   │   ├── useScrapingConfig.ts
│   │   ├── useDataViewer.ts
│   │   └── useAIAnalysis.ts
│   │
│   ├── models/                   # Model Layer (Data & API)
│   │   ├── api/
│   │   │   ├── client.ts         # Axios instance
│   │   │   ├── scrapingApi.ts
│   │   │   ├── dataApi.ts
│   │   │   └── aiApi.ts
│   │   ├── types/
│   │   │   ├── scraping.ts
│   │   │   ├── data.ts
│   │   │   └── ai.ts
│   │   └── transformers/
│   │       └── dataTransformers.ts
│   │
│   ├── shared/                   # Shared utilities
│   │   ├── components/
│   │   ├── hooks/
│   │   └── utils/
│   │
│   ├── App.tsx
│   └── main.tsx
├── package.json
└── tsconfig.json
```

## MVVM Pattern Implementation

### View Layer (Components)
```tsx
// views/ScrapingConfig/ScrapingConfig.tsx
import { useScrapingConfig } from '@/viewmodels/useScrapingConfig';
import { UrlInput } from './components/UrlInput';
import { SelectorBuilder } from './components/SelectorBuilder';

export const ScrapingConfig: React.FC = () => {
  const {
    url,
    setUrl,
    selectors,
    addSelector,
    removeSelector,
    isSubmitting,
    handleSubmit,
    errors
  } = useScrapingConfig();

  return (
    <div className="scraping-config">
      <h1>Configure Scraping Job</h1>
      
      <UrlInput 
        value={url} 
        onChange={setUrl} 
        error={errors.url} 
      />
      
      <SelectorBuilder
        selectors={selectors}
        onAdd={addSelector}
        onRemove={removeSelector}
      />
      
      <button 
        onClick={handleSubmit} 
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Creating...' : 'Start Scraping'}
      </button>
    </div>
  );
};
```

### ViewModel Layer (Custom Hooks)
```tsx
// viewmodels/useScrapingConfig.ts
import { useState, useCallback } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { scrapingApi } from '@/models/api/scrapingApi';
import { ScrapingConfig, Selector } from '@/models/types/scraping';

export const useScrapingConfig = () => {
  const queryClient = useQueryClient();
  const [url, setUrl] = useState('');
  const [selectors, setSelectors] = useState<Selector[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const createJobMutation = useMutation({
    mutationFn: scrapingApi.createJob,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
    }
  });

  const addSelector = useCallback((selector: Selector) => {
    setSelectors(prev => [...prev, selector]);
  }, []);

  const removeSelector = useCallback((index: number) => {
    setSelectors(prev => prev.filter((_, i) => i !== index));
  }, []);

  const validate = useCallback((): boolean => {
    const newErrors: Record<string, string> = {};
    if (!url) newErrors.url = 'URL is required';
    if (selectors.length === 0) newErrors.selectors = 'Add at least one selector';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [url, selectors]);

  const handleSubmit = useCallback(async () => {
    if (!validate()) return;
    
    await createJobMutation.mutateAsync({
      targetUrl: url,
      config: { extractRules: selectors }
    });
  }, [url, selectors, validate, createJobMutation]);

  return {
    url,
    setUrl,
    selectors,
    addSelector,
    removeSelector,
    isSubmitting: createJobMutation.isPending,
    handleSubmit,
    errors
  };
};
```

### Model Layer (API & Types)
```tsx
// models/api/scrapingApi.ts
import { apiClient } from './client';
import { ScrapingJob, CreateJobRequest } from '../types/scraping';

export const scrapingApi = {
  createJob: async (data: CreateJobRequest): Promise<ScrapingJob> => {
    const response = await apiClient.post('/scrape/jobs', data);
    return response.data;
  },

  getJob: async (id: string): Promise<ScrapingJob> => {
    const response = await apiClient.get(`/scrape/jobs/${id}`);
    return response.data;
  },

  listJobs: async (params: { page: number; size: number }) => {
    const response = await apiClient.get('/scrape/jobs', { params });
    return response.data;
  }
};

// models/types/scraping.ts
export interface Selector {
  name: string;
  selector: string;
  type: 'text' | 'html' | 'attribute';
  attribute?: string;
}

export interface CreateJobRequest {
  targetUrl: string;
  config: {
    extractRules: Selector[];
    engine?: 'selenium' | 'playwright';
  };
  schedule?: string;
}

export interface ScrapingJob {
  id: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
  targetUrl: string;
  createdAt: string;
  completedAt?: string;
}
```

## Setup Commands

```bash
# Create React project with Vite
npx create-vite@latest frontend --template react-ts

# Install dependencies
cd frontend
npm install axios @tanstack/react-query react-router-dom
npm install -D @types/react-router-dom

# Start development
npm run dev
```

## Key Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.x",
    "@tanstack/react-query": "^5.x",
    "axios": "^1.x"
  }
}
```
