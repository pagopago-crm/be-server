package com.crm.docs.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.common.response.DataResponse;
import com.crm.docs.common.response.ResponseService;
import com.crm.docs.core.analysis.CodeAnalyze;
import com.crm.docs.dto.req.testcase.TestScenarioReq;
import com.crm.docs.dto.res.testcase.TestCaseRes;
import com.crm.docs.dto.res.testcase.TestScenarioRes;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/*테스트 케이스 생성 및 테스트 시나리오 전달.*/

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test-doc")
public class TestDocController {

	private final CodeAnalyze codeAnalyze;
	private final ResponseService responseService;

	//테스트 케이스 생성.
	@GetMapping("/testcase")
	public DataResponse<TestCaseRes> getTestCase(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo,
		@RequestParam(name = "selectTag") String selectTag
	){

		String result = codeAnalyze.analyzeTestCase(owner, repo, selectTag);

		TestCaseRes testCaseRes = TestCaseRes.builder()
			.testCase(result)
			.build();

		return responseService.getDataResponse(testCaseRes);
	}


	//테스트 시나리오 생성 - 테스트 케이스 받아서
	@SneakyThrows
	@PostMapping("/test_scenario")
	public DataResponse<List<TestScenarioRes>> getTestScenario(
		@RequestBody TestScenarioReq testScenarioReq
	) {

		List<TestScenarioRes> testScenarios = codeAnalyze.analyzeTestScenario(testScenarioReq.getTestCase());

		return responseService.getDataResponse(testScenarios);
	}



}
