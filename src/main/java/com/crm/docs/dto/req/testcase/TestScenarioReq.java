package com.crm.docs.dto.req.testcase;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestScenarioReq {

	private String testCase;

	@Builder
	public TestScenarioReq(String testCase) {
		this.testCase = testCase;
	}
}
