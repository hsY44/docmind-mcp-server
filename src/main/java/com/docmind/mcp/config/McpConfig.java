package com.docmind.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.docmind.mcp.tool.DocumentTools;

@Configuration
public class McpConfig {

	@Bean
	ToolCallbackProvider documentToolCallbacks(DocumentTools documentTools) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(documentTools)
				.build();
	}

	// MCP 엔드포인트만 보호 — 이 앱은 그 외 공개 컨트롤러가 없다
	@Bean
	FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilterRegistration(
			@Value("${docmind.mcp.api-key}") String apiKey) {
		FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>(
				new ApiKeyAuthFilter(apiKey));
		registration.addUrlPatterns("/mcp/*");
		return registration;
	}
}
