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
   'java,database', now()),
  -- 한국어 문서: docmind-rag의 임베딩 모델(nomic-embed-text)이 영어 중심이라
  -- 한국어 질문 데모는 한국어 문서가 있어야 검색 품질이 나온다
  ('스프링 부트 프로파일로 환경 분리하기',
   '스프링 부트는 프로파일 기능으로 로컬, 개발, 운영 환경 설정을 분리한다. application-local.yml처럼 프로파일별 설정 파일을 만들고 spring.profiles.active로 활성화하면, 같은 코드로 환경마다 다른 데이터베이스나 모델 설정을 쓸 수 있다.',
   'spring,java', now()),
  ('RAG 파이프라인의 기본 구조',
   'RAG(검색 증강 생성)는 문서를 청크로 나누고 임베딩해 벡터 저장소에 넣은 뒤, 질문과 유사한 청크를 검색해 LLM 프롬프트에 컨텍스트로 넣는 구조다. 검색 품질이 답변 품질을 좌우하므로 청킹 전략과 임베딩 모델 선택이 중요하다.',
   'ai,rag', now()),
  ('pgvector로 유사도 검색하기',
   'pgvector는 PostgreSQL에서 벡터 타입과 유사도 연산을 지원하는 확장이다. 코사인 거리 기반 최근접 탐색을 SQL로 실행할 수 있고, HNSW 인덱스를 만들면 대량 데이터에서도 빠른 근사 검색이 가능하다.',
   'database,postgres,ai', now());
