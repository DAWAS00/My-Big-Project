package com.webscraper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private AIRequest request;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "tokens_used")
    @Builder.Default
    private Integer tokensUsed = 0;
    
    @Column(name = "latency_ms")
    @Builder.Default
    private Integer latencyMs = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
