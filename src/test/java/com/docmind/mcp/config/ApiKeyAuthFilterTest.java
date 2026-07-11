package com.docmind.mcp.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class ApiKeyAuthFilterTest {

	private final ApiKeyAuthFilter filter = new ApiKeyAuthFilter("expected-key");

	@Test
	void blankExpectedKeyFailsFastInsteadOfDisablingAuth() {
		// 빈 문자열이면 MessageDigest.isEqual(빈 배열, 빈 배열)이 true가 되어 인증이 무력화되므로
		// 조용히 통과시키는 대신 생성 시점에 던져야 한다
		assertThrows(IllegalStateException.class, () -> new ApiKeyAuthFilter(""));
		assertThrows(IllegalStateException.class, () -> new ApiKeyAuthFilter("   "));
	}

	@Test
	void missingHeaderReturns401AndDoesNotCallChain() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		when(response.getWriter()).thenReturn(mock(PrintWriter.class));

		filter.doFilterInternal(request, response, chain);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(chain, never()).doFilter(request, response);
	}

	@Test
	void wrongHeaderReturns401AndDoesNotCallChain() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("X-API-Key")).thenReturn("wrong-key");
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		when(response.getWriter()).thenReturn(mock(PrintWriter.class));

		filter.doFilterInternal(request, response, chain);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(chain, never()).doFilter(request, response);
	}

	@Test
	void correctHeaderCallsChainWithoutSettingErrorStatus() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("X-API-Key")).thenReturn("expected-key");
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, chain);

		verify(chain).doFilter(request, response);
		verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
