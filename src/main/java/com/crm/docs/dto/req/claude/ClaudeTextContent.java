package com.crm.docs.dto.req.claude;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClaudeTextContent extends ClaudeContentItem{

	private String text;

	public ClaudeTextContent(String text) {
		super("text");
		this.text = text;
	}


	public static ClaudeTextContent createClaudeTextContent(String text) {
		return new ClaudeTextContent(text);
	}
}
