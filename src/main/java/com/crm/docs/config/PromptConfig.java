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
	private String mcpScenario;

}
