package com.webscraper.repository;

import com.webscraper.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    
    List<Chunk> findByPageVersionIdOrderByChunkIndexAsc(UUID pageVersionId);
    
    @Query("""
        SELECT c FROM Chunk c 
        JOIN c.pageVersion pv 
        JOIN pv.page p 
        WHERE p.target.id = :targetId
        """)
    List<Chunk> findByTargetId(UUID targetId);
    
    void deleteByPageVersionId(UUID pageVersionId);
}
