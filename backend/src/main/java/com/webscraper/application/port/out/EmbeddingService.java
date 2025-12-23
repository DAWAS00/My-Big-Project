package com.webscraper.application.port.out;

import java.util.List;

/**
 * Port for generating embeddings.
 * Can be implemented by OpenAI, local models, etc.
 */
public interface EmbeddingService {
    
    /**
     * Generate embedding vector for text.
     */
    float[] embed(String text);
    
    /**
     * Batch embedding generation.
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * Get embedding dimension.
     */
    int getDimension();
    
    /**
     * Get model name for tracking.
     */
    String getModelName();
}
