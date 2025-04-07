package com.crm.docs.dto.req.claude;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClaudeFileSource {

	private String type;
	private String mediaType;
	private String data;


	public static ClaudeFileSource createClaudeFileSource(String type, String mediaType, String data) {
		return ClaudeFileSource.builder()
			.type(type)
			.mediaType(mediaType)
			.data(data)
			.build();
	}


}
