# TASKS.md

Working task list. Claude: mark items `[x]` when done, add discovered tasks under the current phase.

## Phase 0 — Setup
- [x] Create Spring Boot project (STS, Spring AI 1.1.8, Java 21)
- [x] Convert `application.properties` → `application.yml`
- [x] Configure MCP server (name, version, Streamable HTTP transport)
- [ ] Set up local PostgreSQL + verify datasource config
- [ ] Verify server starts and MCP endpoint responds

## Phase 1 — Domain
- [x] `Document` entity (id, title, content, tags, createdAt)
- [x] `DocumentRepository` with keyword search query
- [x] Service layer + unit tests

## Phase 2 — MCP Tools
- [x] `DocumentTools` class with `@Tool` methods (search/get/list/save) — follow specs in `docs/PLANNING.md`
- [x] Register via `ToolCallbackProvider` bean
- [x] Input validation on tool parameters
- [ ] Verify tools with MCP Inspector

## Phase 3 — Integration & Polish
- [ ] Connect from a real MCP client (Claude Desktop or docmind-rag) end-to-end
- [ ] Error handling: tool-level failures return useful messages to the LLM
- [ ] Seed data script for demo
- [ ] README.md (architecture diagram, setup, tool docs, demo GIF)

## Backlog (not now)
- Testcontainers integration tests
- Auth for remote deployment
- Deployment (Docker + cloud)
