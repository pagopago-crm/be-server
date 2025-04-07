package com.crm.docs.dto.res.claude;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
public class ClaudeRes {

	private String id;
	private String type;
	private String role;
	private List<ClaudeContentRes> content;
	private String model;
	private String stopReason;
	private ClaudeUsageRes usage;

}
