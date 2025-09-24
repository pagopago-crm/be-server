package com.crm.docs.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.core.mobileMcp.MobileTestAgentService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile-test")
public class TestAgentController {

	private final MobileTestAgentService testService;


	@PostMapping("/execute")
	public ResponseEntity<String> executeTest() {
		log.info("Starting mobile test execution");

		String result = testService.executeTestScenario(null);

		return ResponseEntity.ok(result);
	}
}
