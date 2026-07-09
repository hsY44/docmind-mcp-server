package com.docmind.mcp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.docmind.mcp.domain.Document;
import com.docmind.mcp.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final DocumentRepository repository;

	public List<SearchResult> search(String keyword, int limit) {
		int capped = clamp(limit, 10, 50);
		return repository
				.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
						keyword, keyword, PageRequest.of(0, capped))
				.stream()
				.map(d -> new SearchResult(d.getId(), d.getTitle(), d.getTags(),
						d.getCreatedAt(), snippet(d.getContent(), keyword)))
				.toList();
	}

	public DocumentDetail get(Long id) {
		return repository.findById(id)
				.map(d -> new DocumentDetail(d.getId(), d.getTitle(), d.getContent(),
						d.getTags(), d.getCreatedAt()))
				.orElse(null);
	}

	public DocumentList list(String tag, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), clamp(size, 20, 50));
		Page<Document> result = (tag == null || tag.isBlank())
				? repository.findAll(pageable)
				: repository.findByTagsContainingIgnoreCase(tag, pageable);
		List<DocumentSummary> docs = result.stream()
				.map(d -> new DocumentSummary(d.getId(), d.getTitle(), d.getTags(), d.getCreatedAt()))
				.toList();
		return new DocumentList(docs, result.getTotalElements());
	}

	@Transactional
	public SavedDocument save(String title, String content, String tags) {
		Document saved = repository.save(new Document(title, content, tags));
		return new SavedDocument(saved.getId(), saved.getTitle());
	}

	private static int clamp(int value, int fallbackIfUnset, int max) {
		int v = value <= 0 ? fallbackIfUnset : value;
		return Math.min(v, max);
	}

	// ponytail: ±60-char window around the first case-insensitive match; plenty of context for an LLM
	static String snippet(String content, String keyword) {
		int i = content.toLowerCase().indexOf(keyword.toLowerCase());
		if (i < 0) {
			return content.length() <= 120 ? content : content.substring(0, 120) + "…";
		}
		int start = Math.max(0, i - 60);
		int end = Math.min(content.length(), i + keyword.length() + 60);
		return (start > 0 ? "…" : "") + content.substring(start, end)
				+ (end < content.length() ? "…" : "");
	}

	public record SearchResult(Long id, String title, String tags, LocalDateTime createdAt, String snippet) {}
	public record DocumentDetail(Long id, String title, String content, String tags, LocalDateTime createdAt) {}
	public record DocumentSummary(Long id, String title, String tags, LocalDateTime createdAt) {}
	public record DocumentList(List<DocumentSummary> documents, long total) {}
	public record SavedDocument(Long id, String title) {}
}
