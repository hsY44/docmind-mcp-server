package com.docmind.mcp.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP 엔드포인트(/mcp/*)를 X-API-Key 헤더로 보호한다. 단일 고정 클라이언트(docmind-rag)만
 * 접근하므로 공유 정적 키로 충분 — 값 비교는 타이밍 공격 방지를 위해 상수 시간 비교를 사용한다.
 */
@Slf4j
class ApiKeyAuthFilter extends OncePerRequestFilter {

	private static final String HEADER = "X-API-Key";
	// application.yml의 로컬 개발 기본값과 동일 — 원격 배포에서 그대로 남아있으면 경고
	private static final String LOCAL_DEV_DEFAULT_KEY = "local-dev-api-key";

	private final byte[] expectedKeyBytes;

	ApiKeyAuthFilter(String expectedKey) {
		// 빈 문자열이면 MessageDigest.isEqual(빈 배열, 빈 배열)이 true가 되어 인증이 통째로
		// 무력화된다 — DOCMIND_MCP_API_KEY=(빈 값)처럼 "설정은 됐지만 값이 비어있는" 경우
		// application.yml의 ${...:local-dev-api-key} 기본값은 적용되지 않으므로 별도로 막는다.
		if (expectedKey == null || expectedKey.isBlank()) {
			throw new IllegalStateException("docmind.mcp.api-key must not be blank");
		}
		if (LOCAL_DEV_DEFAULT_KEY.equals(expectedKey)) {
			log.warn("docmind.mcp.api-key가 로컬 개발 기본값입니다 — 원격 배포 환경이라면 "
					+ "DOCMIND_MCP_API_KEY 환경변수로 반드시 교체하세요.");
		}
		this.expectedKeyBytes = expectedKey.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String provided = request.getHeader(HEADER);
		if (provided == null
				|| !MessageDigest.isEqual(provided.getBytes(StandardCharsets.UTF_8), expectedKeyBytes)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"unauthorized\"}");
			return;
		}
		chain.doFilter(request, response);
	}
}
