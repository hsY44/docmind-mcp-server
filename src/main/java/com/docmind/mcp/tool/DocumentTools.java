package com.docmind.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import com.docmind.mcp.service.DocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentTools {

	private final DocumentService service;

	// ponytail: 4개 tool 전체에 대해 try/catch 하나로 처리해서 예외가 MCP 경계를 넘지 않게 함;
	// 메시지는 의도적으로 일반적으로 유지(DB 내부 정보를 LLM에 노출하지 않기 위함), 실제 원인은 로그에 기록.
	private Object run(String action, Supplier<Object> body) {
		try {
			return body.get();
		} catch (Exception e) {
			log.error("Tool '{}' failed", action, e);
			return "Could not complete '" + action + "' due to a server error.";
		}
	}

	// ponytail: 반환 타입이 Object인 이유는 tool이 구조화된 데이터 또는 LLM이 처리할 수 있는
	// 단순 메시지를 반환할 수 있게 하기 위함 — 스펙상 MCP 경계 너머로 예외를 던지는 건 금지됨.

	@Tool(description = "Search documents by keyword against title and content (case-insensitive). "
			+ "Returns matches with a short snippet around the match, not full content.")
	Object searchDocuments(
			@ToolParam(description = "Keyword to search for") String keyword,
			@ToolParam(required = false, description = "Max results, default 10, max 50") Integer limit) {
		if (keyword == null || keyword.isBlank()) {
			return "keyword is required.";
		}
		return run("searchDocuments", () -> {
			var results = service.search(keyword, limit == null ? 10 : limit);
			return results.isEmpty() ? "No documents matched." : results;
		});
	}

	@Tool(description = "Get a single document by id, including its full content.")
	Object getDocument(@ToolParam(description = "Document id") Long id) {
		return run("getDocument", () -> service.get(id)
				.map(Object.class::cast)
				.orElse("No document found with id " + id));
	}

	@Tool(description = "List documents, optionally filtered by tag, with pagination. Never returns content.")
	Object listDocuments(
			@ToolParam(required = false, description = "Tag to filter by") String tag,
			@ToolParam(required = false, description = "Page number, default 0") Integer page,
			@ToolParam(required = false, description = "Page size, default 20, max 50") Integer size) {
		return run("listDocuments", () -> service.list(tag, page == null ? 0 : page, size == null ? 20 : size));
	}

	@Tool(description = "Save a new document. Title max 200 chars, content required. Returns the new id and title.")
	Object saveDocument(
			@ToolParam(description = "Document title, max 200 chars") String title,
			@ToolParam(description = "Document content, plain text or markdown") String content,
			@ToolParam(required = false, description = "Comma-separated tags") String tags) {
		if (title == null || title.isBlank()) {
			return "title is required.";
		}
		if (title.length() > 200) {
			return "title must be 200 characters or fewer.";
		}
		if (content == null || content.isBlank()) {
			return "content is required.";
		}
		return run("saveDocument", () -> service.save(title, content, tags));
	}
}
