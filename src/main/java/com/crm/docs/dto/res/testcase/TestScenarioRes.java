package com.crm.docs.dto.res.testcase;

import java.util.List;

import com.crm.docs.dto.req.testcase.TestScenarioReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestScenarioRes {

	private String title;
	private String description;
	private List<String> steps;
	private String expectedResult;


	@Builder
	public TestScenarioRes(String title, String description, List<String> steps, String expectedResult) {
		this.title = title;
		this.description = description;
		this.steps = steps;
		this.expectedResult = expectedResult;
	}

	public static TestScenarioRes create(String testScenarioRaw) {

		//혹시나 붙었을수도 있는 json 제거.

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(testScenarioRaw, TestScenarioRes.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
