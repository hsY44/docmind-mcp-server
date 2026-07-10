package com.docmind.mcp.tool;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.docmind.mcp.service.DocumentService;

// A service failure must come back as a message, not propagate across the MCP boundary.
class DocumentToolsTest {

	@Test
	void serviceFailureReturnsMessageInsteadOfThrowing() {
		DocumentService service = mock(DocumentService.class);
		when(service.search(anyString(), anyInt())).thenThrow(new RuntimeException("db down"));

		Object result = new DocumentTools(service).searchDocuments("x", null);

		assertInstanceOf(String.class, result);
		assertTrue(((String) result).startsWith("Could not complete"), result.toString());
	}
}
