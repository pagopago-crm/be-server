package com.crm.docs.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.common.response.DataResponse;
import com.crm.docs.common.response.ResponseService;
import com.crm.docs.core.mobileMcp.MobileTestAgentService;
import com.crm.docs.dto.req.testcase.TestScenarioExeReq;
import com.crm.docs.dto.res.testcase.TestScenarioExeRes;
import com.crm.docs.dto.res.testcase.TestScenarioRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile-test")
public class TestAgentController {

	private final MobileTestAgentService testService;
	private final ResponseService responseService;


	@PostMapping("/execute")
	public ResponseEntity<String> executeTest() {
		log.info("Starting mobile test execution");

		String result = testService.executeTestScenario(null);

		return ResponseEntity.ok(result);
	}

	//테스트 시나리오를 받아서 실행하는 메서드
	@PostMapping("/scenario/execute")
	public DataResponse<TestScenarioExeRes> executeScenario(
		@RequestBody TestScenarioExeReq scenarioInfo
	){

		String result = testService.executeTestScenarioMobile(scenarioInfo);

		TestScenarioExeRes testScenarioRes = TestScenarioExeRes.builder()
			.scenarioResult(result)
			.build();

		return responseService.getDataResponse(testScenarioRes);

	}

}
