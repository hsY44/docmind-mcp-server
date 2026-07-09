package com.docmind.mcp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// Pure unit test for the snippet logic — no Spring context, no DB.
class DocumentServiceTest {

	@Test
	void snippetWrapsMatchWithEllipses() {
		String content = "a".repeat(100) + "NEEDLE" + "b".repeat(100);
		String s = DocumentService.snippet(content, "needle");
		assertTrue(s.contains("NEEDLE"), "keeps the matched text");
		assertTrue(s.startsWith("…") && s.endsWith("…"), "trims both sides");
		assertTrue(s.length() < content.length(), "shorter than full content");
	}

	@Test
	void snippetReturnsShortContentAsIsWhenNoMatch() {
		assertEquals("short text", DocumentService.snippet("short text", "zzz"));
	}
}
