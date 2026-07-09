package com.docmind.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
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
}
