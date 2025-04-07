package com.crm.docs.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crm.docs.common.config.PromptConfig;
import com.crm.docs.common.util.FileUtil;
import com.crm.docs.dto.req.claude.ClaudeApiRequest;
import com.crm.docs.dto.req.claude.ClaudeContentItem;
import com.crm.docs.dto.req.claude.ClaudeFileContent;
import com.crm.docs.dto.req.claude.ClaudeFileSource;
import com.crm.docs.dto.req.claude.ClaudeMessage;
import com.crm.docs.dto.req.claude.ClaudeTextContent;
import com.crm.docs.dto.res.GithubBranchRes;
import com.crm.docs.dto.res.claude.ClaudeRes;
import com.crm.docs.infra.ClaudeClient;
import com.crm.docs.infra.GithubClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseDocService {

	private final GithubClient githubClient;
	private final ClaudeClient claudeClient;
	private final PromptConfig promptConfig;

	//클로드에서 사용될 모델을 가져옴 - 설정파일에서 모델 지정하도록 함.
	@Value("${api.claude.model}")
	private String claudeModel;


	//TODO : 테스트 단계에서는 상관없지만 추후에는 비동기식으로 변경해야 한다(MONO사용 필수.)
	//TODO : 현재는 단일 파일을 가져오게 했지만 추후에는 디렉토리 내부 파일을 스캔해서 가져오도록 해야 함.
	public ClaudeRes getRepoInfo(String owner, String repo, String path){

		// GithubBranchRes githubInfo = githubClient.getRepoFileBlock(owner, repo, path).orElseThrow(() -> new RuntimeException("not found github repo file"));
		GithubBranchRes githubInfo = githubClient.getRepoFileBlock(owner, repo, path).get();

		githubInfo.removeContentSpace(); //base64에서 줄바꿈 문자 삭제.

		//파일 확인을 위해 반환된 파일을 다시 저장해봄.
		// base64FileSave(githubInfo);

		log.info(promptConfig.getQaTest());


		//TODO : 프롬프트는 별도로 빼는게 좋을듯.
		/* 요청 형식 만들기 */
		// content필드에 답기.
		List<ClaudeContentItem> contents = Stream.of(
			ClaudeTextContent.createClaudeTextContent(
				promptConfig.getQaTest()
			),//프롬프트 - 명령
			(ClaudeContentItem) ClaudeTextContent.createClaudeTextContent(
				githubInfo.getContent()
			)
		).toList();


		List<ClaudeMessage> messages = Stream.of(
			ClaudeMessage.createClaudeMessage("user", contents)
		).toList();

		//클로드에 요청
		// ClaudeRes claudeResponse = claudeClient.getClaudeTestCaseBlock(
		// 	ClaudeApiRequest.createClaudeApiRequest(
		// 		claudeModel, //설정파일에 등록해둔 클로드 모델 정보
		// 		5000, // Max token수 임시로 설정
		// 		messages
		// 	)
		// ).orElseThrow(() -> new RuntimeException("not found claude response"));

		ClaudeRes claudeResponse = claudeClient.getClaudeTestCaseBlock(
			ClaudeApiRequest.createClaudeApiRequest(
				claudeModel, //설정파일에 등록해둔 클로드 모델 정보
				5000, // Max token수 임시로 설정
				messages
			)
		).get();

		//로컬에 파일로 저장.
		llmResultFileSave(
			claudeResponse.getContent().get(0).getText(),
			"md");

		return claudeResponse;
	}

	//레포 응답값을 파일로 저장.
	private void base64FileSave(GithubBranchRes githubInfo) {
		byte[] decodeByte = Base64.getDecoder().decode(githubInfo.getContent().replaceAll("\\s", ""));

		Path realFileName = Paths.get(githubInfo.getName());

		try(FileChannel fileChannel = FileChannel.open(realFileName,
			StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){

			ByteBuffer buffer = ByteBuffer.wrap(decodeByte);

			fileChannel.write(buffer);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//결과 파일 저장
	private void llmResultFileSave(String content, String ext){

		try{
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
			String timestamp = now.format(formatter);

			//파일 저장 경로 생성.
			String fileName = timestamp + "." + ext;
			Path path = Paths.get("./", fileName);

			log.info("llm file path => {}", path);

			Files.writeString(path, content);
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}
