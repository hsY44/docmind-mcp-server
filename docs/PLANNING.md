# PLANNING.md
Single source of truth for scope, architecture, and specs. Read this before implementing anything.

## Goal
Build an MCP server that exposes document management/search tools over Streamable HTTP,
so that `docmind-rag` (Spring AI RAG service, separate repo) can call them as agent tools.
Portfolio narrative: "designed, built, and deployed a custom MCP server consumed by a separate service."

## Tech Stack
- Java 21, Spring Boot 3.5.16, Gradle (Groovy)
- Spring AI 1.1.8 — `spring-ai-starter-mcp-server-webmvc`
- PostgreSQL, Spring Data JPA, Lombok, Actuator (health check)

## Commands
```bash
./gradlew bootRun   # run server
./gradlew test      # run tests
./gradlew build     # full build
```

## Architecture
```
docmind-rag (MCP client, separate repo)
        │  Streamable HTTP (MCP protocol)
        ▼
docmind-mcp-server (this repo)
        │  JPA
        ▼
PostgreSQL (document metadata + content)
```

## Project Structure
Base package: `com.docmind.mcp`
| Package | Role |
|---------|------|
| `tool/` | MCP tool classes (`@Tool` methods). One class per tool group. |
| `domain/` | JPA entities |
| `repository/` | Spring Data repositories |
| `service/` | business logic between tools and repositories |
| `config/` | MCP server config, `ToolCallbackProvider` bean registration |

## Data Model
`Document`
| Field | Type | Notes |
|-------|------|-------|
| `id` | Long | auto-generated PK |
| `title` | String | required, max 200 |
| `content` | String (TEXT) | required, plain text/markdown |
| `tags` | String | comma-separated, nullable |
| `createdAt` | LocalDateTime | set on save |

## MCP Tool Specs (v1)
All tool descriptions in English, written for LLM consumption.
Errors: never throw raw exceptions across the MCP boundary — return a short message
the LLM can act on (e.g. "No document found with id 42").

### `searchDocuments(keyword, limit)`
- `keyword` (String, required): matched against title and content, case-insensitive
- `limit` (int, optional, default 10, max 50)
- Returns: list of `{id, title, tags, createdAt, snippet}` — snippet is a short excerpt
  around the match, NOT full content
- Empty result → returns the string "No documents matched." (message only, not a list)

### `getDocument(id)`
- `id` (Long, required)
- Returns: `{id, title, content, tags, createdAt}`
- Not found → "No document found with id {id}"

### `listDocuments(tag, page, size)`
- `tag` (String, optional): substring match against the comma-separated tags column,
  case-insensitive (e.g. "java" also matches "javascript") — upgrade to exact tag match
  only if this becomes a problem
- `page` (int, default 0), `size` (int, default 20, max 50)
- Returns: list of `{id, title, tags, createdAt}` + total count. Never returns content.

### `saveDocument(title, content, tags)`
- `title`, `content` required; validated (title ≤ 200 chars, content non-blank)
- Returns: `{id, title}` of the saved document
- Validation failure → return the validation message, don't throw

Scope note: tools are simple and deterministic. Vector/semantic search belongs to
docmind-rag, not here. This server owns raw document storage and keyword retrieval.

## Key Decisions
| Decision | Choice | Why |
|----------|--------|-----|
| Transport | Streamable HTTP (`spring.ai.mcp.server.protocol: STREAMABLE`), not stdio | docmind-rag connects over network |
| DB | PostgreSQL | shared choice across docmind projects |
| Tool registration | `@Tool` methods + `ToolCallbackProvider` bean | Spring AI convention |
| Search (v1) | SQL `ILIKE` keyword search | deterministic, no embedding dependency; FTS/tsvector only if demo needs it |
| Auth | `X-API-Key` header, `Filter` + constant-time compare (`config/ApiKeyAuthFilter`), scoped to `/mcp/*` | single fixed client (docmind-rag) — Basic Auth needs a new `spring-security` dependency for the same shared-secret guarantee, OAuth2 client-credentials solves a multi-client/centralized-identity problem this project doesn't have and Spring AI has no built-in token-refresh support for it |

## Out of Scope (v1)
- Embeddings / vector search (docmind-rag's job)
- File upload parsing (PDF etc.) — plain text/markdown only
- Multi-tenant, rate limiting
- Update/delete tools — add only if docmind-rag needs them

## Verification
- Liveness: `curl http://localhost:8080/actuator/health` (actuator; not behind the API-key filter, which only covers `/mcp/*`)
- Manual: MCP Inspector (`npx @modelcontextprotocol/inspector`) against `http://localhost:8080/mcp`
  — add an `X-API-Key` custom header (see Key Decisions: Auth) or every call returns 401
- Automated: JUnit tests per service method; Testcontainers integration tests (backlog)
- Definition of done per phase: see TASKS.md — a phase is done only when its verify steps pass

## Maintenance
When a decision here changes during implementation, update this file in the same session.
