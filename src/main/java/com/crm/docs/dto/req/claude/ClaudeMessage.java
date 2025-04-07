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
public class ClaudeMessage {

	private String role;
	private List<ClaudeContentItem> content;

	private ClaudeMessage(){}

	public static ClaudeMessage createClaudeMessage(String role, List<ClaudeContentItem> content){
		return ClaudeMessage.builder()
			.role(role)
			.content(content)
			.build();
	}
}
