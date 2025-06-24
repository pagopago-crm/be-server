package com.crm.docs.core.analysis;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.crm.docs.common.config.PromptConfig;
import com.crm.docs.core.GithubFileService;
import com.crm.docs.core.vectorSearch.VectorSearchService;
import com.crm.docs.dto.github.SourceCodeInfoDto;
import com.crm.docs.infra.GithubClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	/**
	 *  태그기반 소스코드로 AI에 테스트 케이스 생성 요청.
	 * @param owner
	 * @param repo
	 * @param selectTags
	 * @return
	 */
	@Override
	public String analyzeSourceCode(String owner, String repo, String selectTags){

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


}
