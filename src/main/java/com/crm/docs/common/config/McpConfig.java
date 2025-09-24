package com.crm.docs.common.config;

import java.util.Collections;
import java.util.List;

import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.modelcontextprotocol.client.McpSyncClient;

@Configuration
public class McpConfig {

	// @Bean
	// @Primary
	public SyncMcpToolCallbackProvider syncMcpToolCallbackProvider(
			@Autowired(required = false) List<McpSyncClient> mcpClients) {
		if (mcpClients == null || mcpClients.isEmpty()) {
			return new SyncMcpToolCallbackProvider(Collections.emptyList());
		}
		return new SyncMcpToolCallbackProvider(mcpClients);
	}
}
