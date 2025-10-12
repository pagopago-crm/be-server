package com.crm.docs.dto.res.testcase;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestCaseRes {

	private String testCase;

	@Builder
	public TestCaseRes(String testCase) {
		this.testCase = testCase;
	}

}
