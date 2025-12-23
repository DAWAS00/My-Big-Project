package com.webscraper.application.port.out;

import java.util.Map;

/**
 * Port for calling Python scraper service.
 * Abstracts HTTP communication with scraper workers.
 */
public interface ScraperGateway {
    
    /**
     * Request scraping of a URL.
     * @return Scraped content and metadata
     */
    ScrapingResult scrape(ScrapeRequest request);
    
    record ScrapeRequest(
        String url,
        String engine,  // "playwright" or "selenium"
        Map<String, Object> config
    ) {}
    
    record ScrapingResult(
        String rawHtml,
        int httpStatus,
        int responseTimeMs,
        String error
    ) {
        public boolean isSuccess() {
            return error == null && httpStatus >= 200 && httpStatus < 300;
        }
    }
}
