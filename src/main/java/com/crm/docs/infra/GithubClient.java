package com.crm.docs.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.crm.docs.dto.res.github.GithubBranchRes;
import com.crm.docs.dto.res.github.GithubContentDto;
import com.crm.docs.dto.res.github.tag.GithubTagRes;
import com.crm.docs.dto.res.github.tag.compare.GithubCompareRes;

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

	/**
	 * 특정 레포의 태그정보 가져오기.
	 */
	public Mono<List<GithubTagRes>> getRepoTags(String owner, String repo){
		return webClient.get()
			.uri("/repos/{owner}/{repo}/tags", owner, repo)
			.retrieve()
			.bodyToFlux(GithubTagRes.class)
			.collectList();
	}

	/**
	 * 태그를 받아서 비교하기. - baseTag 이전태그, compareTag 최신태그
	 */
	public Mono<GithubCompareRes> getRepoCompare(String owner, String repo, String baseTag, String compareTag){
		return webClient.get()
			.uri("/repos/{owner}/{repo}/compare/{baseTag}...{compareTag}", owner, repo, baseTag, compareTag)
			.retrieve()
			.bodyToMono(GithubCompareRes.class);
	}

	/**
	 * content 다운(get 호출)
	 */
	public Mono<GithubContentDto> getContentApi(String url){
		return webClient.get()
			.uri(url)
			.retrieve()
			.bodyToMono(GithubContentDto.class);

	}
}
