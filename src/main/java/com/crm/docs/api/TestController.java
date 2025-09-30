package com.crm.docs.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.core.GithubFileService;
import com.crm.docs.core.analysis.CodeAnalyze;
import com.crm.docs.dto.github.SourceCodeInfoDto;
import com.crm.docs.dto.res.claude.ClaudeRes;
import com.crm.docs.dto.res.github.GithubContentDto;
import com.crm.docs.dto.res.github.tag.GithubTagRes;
import com.crm.docs.dto.res.github.tag.compare.GithubCompareRes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

	private final GithubFileService githubFileService;
	private final CodeAnalyze codeAnalyze;

	@GetMapping("test1")
	public List<GithubTagRes> test1(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo
	){
		return githubFileService.getRepoTagsTest(owner, repo);

	}
	@GetMapping("test2")
	public GithubCompareRes test2(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo,
		@RequestParam(name = "baseTag") String baseTag,
		@RequestParam(name = "compareTag") String compareTag
	){

		return githubFileService.getRepoCompareTest(owner, repo, baseTag, compareTag);
	}

	@GetMapping("test3")
	public ClaudeRes test2(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo,
		@RequestParam(name = "selectTag") String selectTag
	){
		return githubFileService.getChangeFilellm(owner, repo, selectTag);
	}

	@GetMapping("test4")
	public List<SourceCodeInfoDto> test4(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo,
		@RequestParam(name = "selectTag") String selectTag
	){

		return githubFileService.getGithubChangeSource(owner, repo, selectTag);

	}

	/*rag 호출해서 테스트 진행하는 api
	* createRagPromptData 메서드가 rag에서 유사도 측정하여 관련데이터 가져오는 부분.
	* */
	@GetMapping("test5")
	public String test5(
		@RequestParam(name = "owner") String owner,
		@RequestParam(name = "repo") String repo,
		@RequestParam(name = "selectTag") String selectTag
	){

		return codeAnalyze.analyzeSourceCode(owner, repo, selectTag);
	}
}
