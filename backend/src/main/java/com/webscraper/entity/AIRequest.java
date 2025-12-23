package com.webscraper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRequest {
    
    public enum RequestType {
        QUERY, SUMMARY, COMPARE, EXTRACT
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Target target;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String query;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    @Builder.Default
    private RequestType requestType = RequestType.QUERY;
    
    @Column(name = "model_name")
    @Builder.Default
    private String modelName = "gpt-4-turbo";
    
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private Double temperature = 0.7;
    
    @Column(name = "max_tokens")
    @Builder.Default
    private Integer maxTokens = 1000;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
