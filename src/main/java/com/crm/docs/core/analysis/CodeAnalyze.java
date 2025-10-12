package com.crm.docs.core.analysis;

import java.util.List;

import com.crm.docs.dto.res.testcase.TestScenarioRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CodeAnalyze {

	String analyzeSourceCode(String owner, String repo, String selectTags);

	//테스트 케이스 생성.
	String analyzeTestCase(String owner, String repo, String selectTags);

	//테스트 시나리오 생성.
	List<TestScenarioRes> analyzeTestScenario(String testCase) throws JsonProcessingException;
}
