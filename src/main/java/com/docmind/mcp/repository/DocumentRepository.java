package com.docmind.mcp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.docmind.mcp.domain.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	// Case-insensitive keyword search over title + content (LIKE/ILIKE via ContainingIgnoreCase).
	// Pageable applies the limit; no count query is run.
	List<Document> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
			String title, String content, Pageable pageable);

	// ponytail: substring match on the comma-separated tags column, not true exact-tag match
	// (tag "java" would match "javascript"). Upgrade to FIND_IN_SET / a tags join table if that bites.
	Page<Document> findByTagsContainingIgnoreCase(String tag, Pageable pageable);
}
