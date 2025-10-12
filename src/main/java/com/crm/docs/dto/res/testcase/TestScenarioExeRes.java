package com.crm.docs.dto.res.testcase;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestScenarioExeRes {

	private String scenarioResult;


	@Builder
	public TestScenarioExeRes(String scenarioResult) {
		this.scenarioResult = scenarioResult;
	}


}
