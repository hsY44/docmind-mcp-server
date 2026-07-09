package com.docmind.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.docmind.mcp.service.DocumentService;
import com.docmind.mcp.service.DocumentService.DocumentDetail;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentTools {

	private final DocumentService service;

	// ponytail: return type is Object so a tool can hand back structured data OR a plain
	// message the LLM can act on — the spec forbids throwing across the MCP boundary.

	@Tool(description = "Search documents by keyword against title and content (case-insensitive). "
			+ "Returns matches with a short snippet around the match, not full content.")
	Object searchDocuments(
			@ToolParam(description = "Keyword to search for") String keyword,
			@ToolParam(required = false, description = "Max results, default 10, max 50") Integer limit) {
		if (keyword == null || keyword.isBlank()) {
			return "keyword is required.";
		}
		var results = service.search(keyword, limit == null ? 10 : limit);
		return results.isEmpty() ? "No documents matched." : results;
	}

	@Tool(description = "Get a single document by id, including its full content.")
	Object getDocument(@ToolParam(description = "Document id") Long id) {
		DocumentDetail doc = service.get(id);
		return doc != null ? doc : "No document found with id " + id;
	}

	@Tool(description = "List documents, optionally filtered by tag, with pagination. Never returns content.")
	Object listDocuments(
			@ToolParam(required = false, description = "Tag to filter by") String tag,
			@ToolParam(required = false, description = "Page number, default 0") Integer page,
			@ToolParam(required = false, description = "Page size, default 20, max 50") Integer size) {
		return service.list(tag, page == null ? 0 : page, size == null ? 20 : size);
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
		return service.save(title, content, tags);
	}
}
