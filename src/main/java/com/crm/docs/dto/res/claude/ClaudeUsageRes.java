package com.crm.docs.dto.res.claude;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ClaudeUsageRes {

	private String inputTokens;
	private String outputTokens;
}
