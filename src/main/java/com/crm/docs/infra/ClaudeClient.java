package com.crm.docs.infra;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.crm.docs.dto.req.claude.ClaudeApiRequest;
import com.crm.docs.dto.res.claude.ClaudeRes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ClaudeClient {

	private final WebClient webClient;

	public ClaudeClient(@Qualifier("claudeWebClient") WebClient webClient) {
		this.webClient = webClient;
	}

	// 비동기시에 사용
	public Mono<ClaudeRes> getClaudeTestCase(ClaudeApiRequest claudeApiRequest){
		return webClient.post()
			.bodyValue(claudeApiRequest)
			.retrieve()
			.bodyToMono(ClaudeRes.class);
	}

	//동기방식
	public Optional<ClaudeRes> getClaudeTestCaseBlock(ClaudeApiRequest claudeApiRequest) {

		try{
			ObjectMapper objectMapper = new ObjectMapper();
			log.info("claudeApiRequest: {}", objectMapper.writeValueAsString(claudeApiRequest));
			return Optional.ofNullable(
				webClient.post()
					.bodyValue(claudeApiRequest)
					.retrieve()
					.bodyToMono(ClaudeRes.class)
					.block()
			);
		} catch (Exception e){
			return Optional.empty();
		}

	}

}
