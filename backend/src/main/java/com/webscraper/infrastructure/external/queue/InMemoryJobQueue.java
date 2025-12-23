package com.webscraper.infrastructure.external.queue;

import com.webscraper.application.port.out.JobQueue;
import com.webscraper.domain.entity.ScrapeJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In-memory job queue for development.
 * Swap with RabbitMQ/Redis for production.
 */
@Component
public class InMemoryJobQueue implements JobQueue {
    
    private final BlockingQueue<ScrapeJob> queue = new LinkedBlockingQueue<>();

    @Override
    public void enqueue(ScrapeJob job) {
        queue.offer(job);
    }

    @Override
    public ScrapeJob dequeue() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public void acknowledge(ScrapeJob job) {
        // No-op for in-memory queue
    }

    @Override
    public void requeue(ScrapeJob job) {
        queue.offer(job);
    }
}
