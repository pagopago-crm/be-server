package com.crm.docs.dto.req.claude;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClaudeFileContent extends ClaudeContentItem{

	private ClaudeFileSource source;

	public ClaudeFileContent(String type, ClaudeFileSource source) {
		super(type);
		this.source = source;
	}


	public static ClaudeFileContent createClaudeFileContent(String type, ClaudeFileSource source) {
		return new ClaudeFileContent(type, source);
	}


}
