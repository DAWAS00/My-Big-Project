package com.webscraper.application.port.out;

import com.webscraper.domain.entity.ScrapeJob;

/**
 * Port for job queue operations.
 * Can be implemented by RabbitMQ, Redis, Kafka, or in-memory.
 */
public interface JobQueue {
    
    /**
     * Add a job to the queue for processing.
     */
    void enqueue(ScrapeJob job);
    
    /**
     * Get next job from queue (for workers).
     */
    ScrapeJob dequeue();
    
    /**
     * Acknowledge job completion.
     */
    void acknowledge(ScrapeJob job);
    
    /**
     * Return job to queue (on failure).
     */
    void requeue(ScrapeJob job);
}
