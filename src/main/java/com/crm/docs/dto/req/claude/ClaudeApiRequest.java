package com.crm.docs.dto.req.claude;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClaudeApiRequest {

	private String model;
	private int maxTokens;
	private List<ClaudeMessage> messages;

	private ClaudeApiRequest(){}

	public static ClaudeApiRequest createClaudeApiRequest(String model, int maxTokens, List<ClaudeMessage> messages) {
		return ClaudeApiRequest.builder()
			.model(model)
			.maxTokens(maxTokens)
			.messages(messages)
			.build();
	}

}
