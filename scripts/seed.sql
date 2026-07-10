-- Demo seed for docmind-mcp-server. Run against local Postgres AFTER the app has booted once
-- (ddl-auto creates the `document` table). Re-runnable: resets to this known set.
--   docker compose exec -T postgres psql -U docmind -d docmind < scripts/seed.sql
TRUNCATE TABLE document RESTART IDENTITY;
INSERT INTO document (title, content, tags, created_at) VALUES
  ('Getting Started with Spring Boot',
   'Spring Boot lets you build stand-alone, production-grade Spring applications with minimal configuration. Autoconfiguration wires sensible defaults so you can focus on business logic instead of boilerplate setup.',
   'spring,java', now()),
  ('PostgreSQL Indexing Basics',
   'PostgreSQL uses B-tree indexes by default to speed up equality and range lookups. Case-insensitive keyword search with ILIKE benefits from expression indexes, and EXPLAIN ANALYZE reveals whether a query hits an index or falls back to a sequential scan.',
   'database,postgres', now()),
  ('What is the Model Context Protocol',
   'The Model Context Protocol (MCP) is an open standard that lets AI applications call external tools over a transport such as Streamable HTTP. A server exposes tools with typed parameters that an LLM can invoke to fetch data or take actions.',
   'mcp,ai', now()),
  ('Spring AI Tool Calling',
   'Spring AI turns annotated methods into LLM-callable tools. Mark a method with @Tool, register the bean through a ToolCallbackProvider, and the framework advertises the tool and routes invocations to your code, converting arguments and results automatically.',
   'spring,ai,mcp', now()),
  ('JPA and Hibernate Notes',
   'JPA maps Java entities to relational tables, and Hibernate is the default provider in Spring Boot. Transactions demarcated with @Transactional keep persistence operations atomic, while a repository abstraction hides the boilerplate of loading and saving entities.',
   'java,database', now());
