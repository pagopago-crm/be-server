package com.crm.docs.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.docs.core.TestCaseDocService;
import com.crm.docs.dto.res.GithubBranchRes;
import com.crm.docs.dto.res.claude.ClaudeRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DocController {

	private final TestCaseDocService testCaseDocService;

	//TODO : 추후에는 레포와 브랜치명을 받아서 처리해야 하고, 레포와 브랜치를 검색할 수 있는 api도 필요.
	/*선택한 레포와 브랜치명 또는 해시 코드 값을 받아서 해당 레포내의 파일을 받아서 분석 후, 결과를 표로 만들어 반환해주는 api*/
	@GetMapping("/analyze")
	public ClaudeRes getAnalyzeCode(
		@RequestParam String owner,
		@RequestParam String repo,
		@RequestParam String path
	){

		return testCaseDocService.getRepoInfo(owner, repo, path);
	}
}
