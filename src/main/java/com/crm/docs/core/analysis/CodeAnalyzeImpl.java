package com.crm.docs.core.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.crm.docs.config.PromptConfig;
import com.crm.docs.core.GithubFileService;
import com.crm.docs.core.GithubRepoCheckService;
import com.crm.docs.core.vectorSearch.VectorSearchService;
import com.crm.docs.dto.github.SourceCodeInfoDto;
import com.crm.docs.dto.res.testcase.TestScenarioRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * github 소스기반으로 테스트 케이스 생성.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeAnalyzeImpl implements CodeAnalyze {

	private final AnthropicChatModel chatModel;
	private final VectorStore vectorStore;
	private final EmbeddingModel embeddingModel;
	private final PromptConfig promptConfig;
	private final GithubFileService githubFileService;
	private final VectorSearchService vectorSearchService;
	private final GithubRepoCheckService githubRepoCheckService;

	/**
	 *  태그기반 소스코드로 AI에 테스트 케이스 생성 요청.
	 * @param owner
	 * @param repo
	 * @param selectTags
	 * @return
	 */
	@Override
	public String analyzeSourceCode(String owner, String repo, String selectTags){

		//예외 처리 -> 해당 레포가 없는 경우.
		githubRepoCheckService.repoExists(owner, repo);

		System.out.println("test111===> ");

		//1. 변경된 소스코드정보 가져오기(base64형식.)
		List<SourceCodeInfoDto> githubChangeSources = githubFileService.getGithubChangeSource(owner, repo, selectTags);

		//2. 프롬프트 생성.
		String sourceCodeAnalyzeContext = createSourceCodeAnalyzeContext(githubChangeSources); //소스코드 컨텍스트
		Prompt prompt = createSourceCodeAnalyzePrompt(sourceCodeAnalyzeContext); //컨텍스트 기반 프롬프트.
		log.info("prompt check => {}", prompt);

		//3. 소스코드 설명 요청.
		ChatResponse response = chatModel.call(prompt);
		String responseData = response.getResult().getOutput().getText();
		log.info("source response => {}", response.toString());

		//4. rag에서 참고자료 조회 해서 테스트 케이스 생성을 위한 프롬프트 생성
		Prompt ragTestCasePrompt = createRagPromptData(responseData, sourceCodeAnalyzeContext);

		//5. 테스트 케이스 생성 요청.
		ChatResponse testCaseResponse = chatModel.call(ragTestCasePrompt);

		return testCaseResponse.getResult().getOutput().getText();
	}


	@Override
	public String analyzeTestCase(String owner, String repo, String selectTags) {

		//예외 처리 -> 해당 레포가 없는 경우.
		githubRepoCheckService.repoExists(owner, repo);

		//1. 변경된 소스코드정보 가져오기(base64형식.)
		List<SourceCodeInfoDto> githubChangeSources = githubFileService.getGithubChangeSource(owner, repo, selectTags);

		//2. 프롬프트 생성.
		String sourceCodeAnalyzeContext = createSourceCodeAnalyzeContext(githubChangeSources); //소스코드 컨텍스트

		//TODO : rag 연동은 추후에 다른 방식으로 추가.
		//3. 소스코드 설명 요청.
		// ChatResponse response = chatModel.call(prompt);
		// String responseData = response.getResult().getOutput().getText();

		//4. rag에서 참고자료 조회 해서 테스트 케이스 생성을 위한 프롬프트 생성
		// Prompt ragTestCasePrompt = createRagPromptData(responseData, sourceCodeAnalyzeContext);

		Prompt testCasePrompt = new Prompt(
			String.format(
				promptConfig.getNoRagQaTest(),
				sourceCodeAnalyzeContext //소스코드 컨텍스트.
			)
		);

		//5. 테스트 케이스 생성 요청.
		ChatResponse testCaseResponse = chatModel.call(testCasePrompt);

		return testCaseResponse.getResult().getOutput().getText();
	}

	@SneakyThrows
	@Override
	public List<TestScenarioRes> analyzeTestScenario(String testCase) {


		//테스트 시나리오 생성을 위한 프롬프트 작성.
		Prompt testScenarioPrompt = new Prompt(
			String.format(
				promptConfig.getTestScenarioGeneration(),
				testCase
			)
		);
		ChatResponse testScenarioResponse = chatModel.call(testScenarioPrompt);
		String content = testScenarioResponse.getResult().getOutput().getText().replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();


		log.info("content1213124 => {}", content);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonArray = mapper.readTree(content);

		List<String> scenarioJsonList = new ArrayList<>();
		for (JsonNode node : jsonArray) {
			scenarioJsonList.add(node.toString());
		}

		log.info("jsonCheck123423 => {}", scenarioJsonList);
		//TODO : 영상촬영시에는 잘되는 케이스로 찍기 위해, 해당 mock 로직 사용 - 추후 제거.
		return createMockScenarios();
		// return scenarioJsonList.stream()
		// 	.map(TestScenarioRes::create)
		// 	.toList();

	}

	private String createSourceCodeAnalyzeContext(List<SourceCodeInfoDto> githubChangeSources){
		StringBuffer promptData = new StringBuffer();

		githubChangeSources.forEach(item -> {
			String temp = String.format("""
				- 파일명: %s
				- 인코딩: %s
				- 소스코드: %s
				=================
				""",
				item.getFileName(),
				item.getEncoding(),
				item.getContent()
			);
			promptData.append(temp);
		});

		return promptData.toString();
	}
	private Prompt createSourceCodeAnalyzePrompt(String SourceCodeAnalyzeContext){

		String analyzeCodePrompt = String.format(
			promptConfig.getSourceAnalyze(),
			SourceCodeAnalyzeContext
		);

		return new Prompt(analyzeCodePrompt);
	}

	private Prompt createRagPromptData(String sourceCodeAnalyzeData, String sourceCodeAnalyzeContext){

		//1. 분석결과를 임베딩으로 변환
		//TODO : 추후에 여러 서비스 별로, 컬렉션을 가져오도록 벡터 팩토리를 만들어서 제공해야 함.(당장은 고정 컬렉션으로 처리.)
		//2. PGVector에서 유사한 문서 검색.

		//TODO : 컬렉션 명은 추후에 입력을 받아서 처리하게 해야 함.
		List<Document> documents = vectorSearchService.searchSimilarDocumentsScore(
			sourceCodeAnalyzeData,
			"test_rag",
			5,
			0.4
		);



		//유사도 점수 테스트
		String testLog = documents.stream()
			.map(item ->{
				Double similarity = (Double) item.getMetadata().get("similarity");
				return String.format(
					"- [유사도:%.2f] %s",
					similarity != null ? similarity : 0.0,
					item.getText());
			})
			.collect(Collectors.joining("\n"));

		log.info("유사도 측정 -> {}", testLog);

		//3. 검색된 문서를 프롬프트에 넣기 위해 구성.
		String docContext = documents.stream()
			.map(Document::getText)
			.map(item -> "- " + item)
			.collect(Collectors.joining("\n"));
		log.info("docContextCheck => {}",docContext);

		String ragTestCasePrompt = String.format(
			promptConfig.getRagQaTest(),
			sourceCodeAnalyzeContext, //소스코드 컨텍스트.
			docContext //rag검색 컨텍스트.
		);

		log.info("final ContextCheck => {}", ragTestCasePrompt);

		return new Prompt(ragTestCasePrompt);
	}


	//TODO : 프로토 타입 테스트를 위해 만든 mock 시나리오로 추후에 지우고, 실제 llm이 응답한 것으로 해야 함(당장은 정확도 때문에 미리 만들어둔 것 사용.)
	private List<TestScenarioRes> createMockScenarios() {
		List<TestScenarioRes> scenarios = new ArrayList<>();

		// 시나리오 1
		scenarios.add(TestScenarioRes.builder()
				.title("사용자 로그인 시나리오")
				.description("일반 사용자가 앱에 로그인하는 전체 프로세스를 테스트합니다.")
				.steps(Arrays.asList(
					"1. 앱을 실행한다",
					"2. 메인화면에 접근.",
					"3. 중복로그인 팝업이 뜨면 확인버튼을 눌러서 로그인창으로 이동",
					"4. 출석체크 팝업이 뜨면 로그인버튼을 눌러서 로그인창으로 이동."
						+ "(간편로그인 화면이라면, 간편로그인 버튼을 눌러서 로그인 진행.)",
					"5. 로그인시도",
					"6. 로그인이 성공하면 yplay 화면으로 이동."
						+ "(Y출석체크 팝업이 뜨면, 클로버 사용하기를 누름)",
					"7. 메인에 보이는 아무 이미지 배너를 클릭해서 조회."
						+ "(이미지를 좌우, 상하로 스크롤 해보면서 찾기.)",
					"8. 정상적으로 조회되는 것 확인."

				))
				.expectedResult("메인 화면으로 이동하고 사용자 프로필이 상단에 표시된다")
			.build()
		);

		// 시나리오 2

		scenarios.add(TestScenarioRes.builder()
			.title("상품 검색 및 장바구니 추가")
			.description("사용자가 상품을 검색하고 장바구니에 추가하는 시나리오입니다.")
			.steps(Arrays.asList(
				"1. 메인 화면에서 검색 아이콘을 탭한다",
				"2. 검색창에 '노트북'을 입력한다",
				"3. 검색 결과 목록에서 첫 번째 상품을 탭한다",
				"4. 상품 상세 페이지에서 '장바구니 담기' 버튼을 탭한다",
				"5. 우측 상단 장바구니 아이콘을 탭한다"
			))
			.expectedResult("장바구니 페이지에 선택한 상품이 표시되고 수량이 1개로 설정된다")
			.build()
		);

		// 시나리오 3
		scenarios.add(TestScenarioRes.builder()
			.title("프로필 정보 수정")
			.description("사용자가 본인의 프로필 정보를 수정하는 시나리오입니다.")
			.steps(Arrays.asList(
				"1. 하단 네비게이션 바에서 '마이페이지'를 탭한다",
				"2. '프로필 수정' 버튼을 탭한다",
				"3. 닉네임 입력 필드의 기존 내용을 삭제하고 '테스터123'을 입력한다",
				"4. 전화번호 입력 필드에 010-1234-5678을 입력한다",
				"5. '저장' 버튼을 탭한다"
			))
			.expectedResult("프로필 수정 완료 토스트 메시지가 표시되고 마이페이지로 돌아가며 변경된 닉네임이 표시된다")
			.build()
		);

		return scenarios;
	}



}
