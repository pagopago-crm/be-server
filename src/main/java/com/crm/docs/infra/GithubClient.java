package com.crm.docs.infra;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.crm.docs.dto.res.GithubBranchRes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GithubClient {


	private final WebClient webClient;

	public GithubClient(@Qualifier("githubWebClient") WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<GithubBranchRes> getRepoFile(String owner,String repo, String path){
		return webClient.get()
			.uri("/repos/{owner}/{repo}/contents/{path}?ref=main", owner, repo, path)
			.retrieve()
			.bodyToMono(GithubBranchRes.class);
	}

	//TODO : ref에 원하는 브랜치명 붙여야 함(문서에는 없는데, 안붙이면 파일이 안가져와짐.)
	public Optional<GithubBranchRes> getRepoFileBlock(String owner,String repo, String path){
		try {
			log.info("repo => {}, owner => {}, path => {}", repo, owner, path);
			return Optional.ofNullable(
				webClient.get()
					.uri("/repos/{owner}/{repo}/contents/{path}?ref=main", owner, repo, path)
					.retrieve()
					.bodyToMono(GithubBranchRes.class)
					.block()
			);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
