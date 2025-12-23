package com.webscraper.application.port.out;

import java.util.List;

/**
 * Port for LLM operations.
 * Can be implemented by OpenAI, Anthropic, local models, etc.
 */
public interface LLMService {
    
    /**
     * Generate response using RAG context.
     */
    LLMResponse generate(LLMRequest request);
    
    record LLMRequest(
        String query,
        List<String> context,  // Retrieved chunks
        String systemPrompt,
        double temperature,
        int maxTokens
    ) {}
    
    record LLMResponse(
        String content,
        int tokensUsed,
        int latencyMs
    ) {}
}
