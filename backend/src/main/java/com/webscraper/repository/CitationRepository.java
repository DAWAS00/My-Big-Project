package com.webscraper.repository;

import com.webscraper.entity.Citation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CitationRepository extends JpaRepository<Citation, UUID> {
    
    List<Citation> findByResponseIdOrderByCitationOrderAsc(UUID responseId);
    
    @Query("""
        SELECT c, ch, pv, p 
        FROM Citation c 
        JOIN c.chunk ch 
        JOIN ch.pageVersion pv 
        JOIN pv.page p 
        WHERE c.response.id = :responseId 
        ORDER BY c.citationOrder
        """)
    List<Object[]> findCitationsWithSourceByResponseId(UUID responseId);
}
