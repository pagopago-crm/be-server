package com.crm.docs.dto.req.claude;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


public abstract class ClaudeContentItem {

	private String type;

	public ClaudeContentItem(String type){
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

}
