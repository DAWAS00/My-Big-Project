# Python Scraping Service Guide

## Project Structure

```
scraper/
├── app/
│   ├── __init__.py
│   ├── main.py                # Flask application entry
│   │
│   ├── engines/
│   │   ├── __init__.py
│   │   ├── base.py            # Abstract scraper
│   │   ├── selenium_engine.py
│   │   └── playwright_engine.py
│   │
│   ├── parsers/
│   │   ├── __init__.py
│   │   ├── html_parser.py
│   │   └── json_parser.py
│   │
│   ├── api/
│   │   ├── __init__.py
│   │   └── routes.py
│   │
│   └── config/
│       └── settings.py
│
├── requirements.txt
├── Dockerfile
└── docker-compose.yml
```

## Core Components

### Flask API
```python
# app/main.py
from flask import Flask
from app.api.routes import scraping_bp

def create_app():
    app = Flask(__name__)
    app.register_blueprint(scraping_bp, url_prefix='/api')
    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=5000)
```

### API Routes
```python
# app/api/routes.py
from flask import Blueprint, request, jsonify
from app.engines import get_engine

scraping_bp = Blueprint('scraping', __name__)

@scraping_bp.route('/scrape', methods=['POST'])
def scrape():
    data = request.json
    url = data.get('url')
    config = data.get('config', {})
    engine_type = config.get('engine', 'playwright')
    
    engine = get_engine(engine_type)
    
    try:
        result = engine.scrape(url, config)
        return jsonify({
            'success': True,
            'data': result
        })
    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500

@scraping_bp.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'healthy'})
```

### Base Engine
```python
# app/engines/base.py
from abc import ABC, abstractmethod
from typing import Any, Dict, List

class BaseScraper(ABC):
    @abstractmethod
    def scrape(self, url: str, config: Dict[str, Any]) -> List[Dict]:
        """Scrape the given URL with configuration"""
        pass
    
    def extract_data(self, page, rules: List[Dict]) -> Dict:
        """Extract data based on selector rules"""
        result = {}
        for rule in rules:
            name = rule['name']
            selector = rule['selector']
            rule_type = rule.get('type', 'text')
            
            elements = page.query_selector_all(selector)
            
            if rule_type == 'text':
                result[name] = [el.text_content() for el in elements]
            elif rule_type == 'html':
                result[name] = [el.inner_html() for el in elements]
            elif rule_type == 'attribute':
                attr = rule.get('attr', 'href')
                result[name] = [el.get_attribute(attr) for el in elements]
        
        return result
```

### Playwright Engine
```python
# app/engines/playwright_engine.py
from playwright.sync_api import sync_playwright
from .base import BaseScraper
from typing import Any, Dict, List

class PlaywrightScraper(BaseScraper):
    def scrape(self, url: str, config: Dict[str, Any]) -> List[Dict]:
        results = []
        
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            context = browser.new_context(
                user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...'
            )
            page = context.new_page()
            
            try:
                page.goto(url, wait_until='networkidle')
                
                # Wait for specific selector if configured
                if wait_selector := config.get('waitForSelector'):
                    page.wait_for_selector(wait_selector, timeout=10000)
                
                # Extract data
                rules = config.get('extractRules', [])
                data = self.extract_data(page, rules)
                results.append(data)
                
                # Handle pagination
                pagination = config.get('pagination', {})
                if pagination.get('enabled'):
                    max_pages = pagination.get('maxPages', 5)
                    next_selector = pagination.get('nextSelector')
                    
                    for _ in range(max_pages - 1):
                        next_btn = page.query_selector(next_selector)
                        if not next_btn:
                            break
                        
                        next_btn.click()
                        page.wait_for_load_state('networkidle')
                        
                        data = self.extract_data(page, rules)
                        results.append(data)
                        
            finally:
                browser.close()
        
        return results
```

### Selenium Engine
```python
# app/engines/selenium_engine.py
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from .base import BaseScraper

class SeleniumScraper(BaseScraper):
    def scrape(self, url: str, config: dict) -> list:
        options = Options()
        options.add_argument('--headless')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        
        driver = webdriver.Chrome(options=options)
        results = []
        
        try:
            driver.get(url)
            
            if wait_selector := config.get('waitForSelector'):
                WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, wait_selector))
                )
            
            rules = config.get('extractRules', [])
            data = self._extract_selenium(driver, rules)
            results.append(data)
            
        finally:
            driver.quit()
        
        return results
    
    def _extract_selenium(self, driver, rules: list) -> dict:
        result = {}
        for rule in rules:
            name = rule['name']
            selector = rule['selector']
            elements = driver.find_elements(By.CSS_SELECTOR, selector)
            
            if rule.get('type') == 'attribute':
                attr = rule.get('attr', 'href')
                result[name] = [el.get_attribute(attr) for el in elements]
            else:
                result[name] = [el.text for el in elements]
        
        return result
```

## Docker Setup

### Dockerfile
```dockerfile
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies for Playwright
RUN apt-get update && apt-get install -y \
    wget gnupg && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN playwright install chromium --with-deps

COPY app/ ./app/

EXPOSE 5000
CMD ["python", "-m", "app.main"]
```

### requirements.txt
```
flask==3.0.0
playwright==1.40.0
selenium==4.15.0
webdriver-manager==4.0.1
beautifulsoup4==4.12.2
lxml==4.9.3
```

## Setup Commands
```bash
# Create virtual environment
python -m venv venv
source venv/bin/activate  # Linux/Mac
.\venv\Scripts\activate   # Windows

# Install dependencies
pip install -r requirements.txt
playwright install chromium

# Run service
python -m app.main
```
