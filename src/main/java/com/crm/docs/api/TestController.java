package com.crm.docs.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.core.GithubFileService;
import com.crm.docs.dto.res.claude.ClaudeRes;
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
}
