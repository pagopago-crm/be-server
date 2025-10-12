package com.crm.docs.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crm.docs.config.PromptConfig;
import com.crm.docs.common.util.FileUtil;
import com.crm.docs.dto.github.SourceCodeInfoDto;
import com.crm.docs.dto.req.claude.ClaudeApiRequest;
import com.crm.docs.dto.req.claude.ClaudeContentItem;
import com.crm.docs.dto.req.claude.ClaudeMessage;
import com.crm.docs.dto.req.claude.ClaudeTextContent;
import com.crm.docs.dto.res.claude.ClaudeRes;
import com.crm.docs.dto.res.github.GithubContentDto;
import com.crm.docs.dto.res.github.tag.GithubTagRes;
import com.crm.docs.dto.res.github.tag.compare.GithubCompareRes;
import com.crm.docs.infra.ClaudeClient;
import com.crm.docs.infra.GithubClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 변경된 파일 가져오기.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFileService {

	private final GithubClient githubClient;
	private final ClaudeClient claudeClient;
	private final PromptConfig promptConfig;

	//클로드에서 사용될 모델을 가져옴 - 설정파일에서 모델 지정하도록 함.
	@Value("${api.claude.model}")
	private String claudeModel;

	public List<GithubTagRes> getRepoTagsTest(String owner, String repo){
		return githubClient.getRepoTags(owner, repo).block();
	}

	public GithubCompareRes getRepoCompareTest(String owner, String repo, String baseTag, String compareTag){
		return githubClient.getRepoCompare(owner, repo, baseTag, compareTag).block();
	}

	/**
	 * 이전 태그와 비교해서 변경된 소스 파일만 가져오기
	 */
	public List<SourceCodeInfoDto> getGithubChangeSource(String owner, String repo, String selectTags){
		//1. 모든 태그정보 가져오기.
		List<GithubTagRes> tags = githubClient.getRepoTags(owner, repo).block();

		//TODO : api 테스트 결과 태그가 하나일때 ^ 해당 기호를 붙이는 방식으로는 파일이 없는 걸로 나와서 해결필요.
		//2. 대상 태그 찾기.
		String baseTag = tags.stream()
			.filter(tag -> !tag.getName().equals(selectTags))
			.findFirst()
			.map(GithubTagRes::getName)//이전 태그가 있으면 반환.
			.orElse(selectTags + "^"); //이전태그가 없으면 최초 생성된 태그로 자기 자신과 비교.

		//3. 선택한 태그와 추출한 태그로 변경된 파일조회
		GithubCompareRes githubCompareRes = githubClient.getRepoCompare(owner, repo, baseTag, selectTags).block();


		//4. 추출한 정보로 파일정보 가져오기
		List<GithubContentDto> githubBranchResList = githubCompareRes.getFiles().stream()
			.filter(file -> file.getContentsUrl() != null) //컨텐츠 url이 없으면 패스.
			.map(file -> {
				String remakeUrl = FileUtil.removeGithubUrl(file.getContentsUrl());
				return githubClient.getContentApi(remakeUrl).block();
			})
			.toList();

		//5. 줄바꿈 문자 제거 및 필요데이터만 추출.
		return githubBranchResList.stream()
			.map(item -> {
				item.removeContentSpace();
				return item;
			})
			.map(SourceCodeInfoDto::of)
			.toList();
	}


	/**
	 * 이전 태그와 비교해서 변경된 파일로 분석 요청
	 */
	public ClaudeRes getChangeFilellm(String owner, String repo, String selectTags){
		//1. 모든 태그정보 가져오기.
		List<GithubTagRes> tags = githubClient.getRepoTags(owner, repo).block();

		//TODO : api 테스트 결과 태그가 하나일때 ^ 해당 기호를 붙이는 방식으로는 파일이 없는 걸로 나와서 해결필요.
		//2. 대상 태그 찾기.
		String baseTag = tags.stream()
			.filter(tag -> !tag.getName().equals(selectTags))
			.findFirst()
			.map(GithubTagRes::getName)//이전 태그가 있으면 반환.
			.orElse(selectTags + "^"); //이전태그가 없으면 최초 생성된 태그로 자기 자신과 비교.

		log.info("test =>>> {}",baseTag);
		//3. 선택한 태그와 추출한 태그로 변경된 파일조회
		GithubCompareRes githubCompareRes = githubClient.getRepoCompare(owner, repo, baseTag, selectTags).block();

		//4. 추출한 정보로 파일정보 가져오기
		List<GithubContentDto> githubBranchResList = githubCompareRes.getFiles().stream()
			.filter(file -> file.getContentsUrl() != null) //컨텐츠 url이 없으면 패스.
			.map(file -> {
				String remakeUrl = FileUtil.removeGithubUrl(file.getContentsUrl());
				return githubClient.getContentApi(remakeUrl).block();
			})
			.toList();

		//테스트를 위해 파일 저장.
		// githubBranchResList.forEach(res -> {
		// 		base64FileSave(res.getContent(),res.getName());
		// 	});

		//5. 클로드 요청을 위해서 형식 만들기.
		List<ClaudeContentItem> contents = new ArrayList<>();
		//프롬프트 추가.
		contents.add(ClaudeTextContent.createClaudeTextContent(promptConfig.getQaTest()));
		//코드 추가.
		githubBranchResList.stream()
			.map(item -> {
				item.removeContentSpace();

				return (ClaudeContentItem) ClaudeTextContent.createClaudeTextContent(item.getContent());
			})
			.forEach(contents::add);

		List<ClaudeMessage> messages = Stream.of(
			ClaudeMessage.createClaudeMessage("user", contents)
		).toList();


		//클로드에 요청
		ClaudeRes claudeRes = claudeClient.getClaudeTestCaseBlock(
			ClaudeApiRequest.createClaudeApiRequest(
				claudeModel, //설정파일에 등록해둔 클로드 모델 정보
				5000, // Max token수 임시로 설정
				messages
			)
		).get();

		//로컬에 파일로 저장.
		llmResultFileSave(
			claudeRes.getContent().get(0).getText(),
			"md");


		return claudeRes;

	}

	//결과 파일 저장
	private void llmResultFileSave(String content, String ext) {

		try {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
			String timestamp = now.format(formatter);

			//파일 저장 경로 생성.
			String fileName = timestamp + "." + ext;
			Path path = Paths.get("./", fileName);

			log.info("llm file path => {}", path);

			Files.writeString(path, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void base64FileSave(String content, String filename){
		byte[] decodeByte = Base64.getDecoder().decode(content.replaceAll("\\s", ""));

		Path realFileName = Paths.get(filename);

		try(FileChannel fileChannel = FileChannel.open(realFileName,
			StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){

			ByteBuffer buffer = ByteBuffer.wrap(decodeByte);

			fileChannel.write(buffer);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
