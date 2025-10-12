package com.crm.docs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix="prompt")
@Getter
@Setter
public class PromptConfig {

	private String qaTest;
	private String sourceAnalyze;
	private String ragQaTest;
	private String mcpUserPrompt; //mcp를 사용한 자동테스트 시에 사용할 프롬프트
	private String noRagQaTest;
	private String testScenarioGeneration;
	private String mcpSystemPrompt;

}
