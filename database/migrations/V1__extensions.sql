-- ============================================================================
-- V1__extensions.sql
-- Enable required PostgreSQL extensions
-- ============================================================================

-- UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Vector similarity search for RAG embeddings
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================================
-- Done when: SELECT count(*) FROM pg_extension WHERE extname IN ('uuid-ossp', 'vector');
-- Expected: 2
-- ============================================================================
