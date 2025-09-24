package com.crm.docs.core.mobileMcp;

import org.slf4j.Logger;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import io.modelcontextprotocol.spec.McpError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobileTestAgentService {

	private final ChatClient chatClient;

	public MobileTestAgentService(AnthropicChatModel chatModel,
		SyncMcpToolCallbackProvider mcpToolCallbackProvider){

		ToolCallback[] toolCallbacks = mcpToolCallbackProvider.getToolCallbacks();

		log.info("=== 사용 가능한 MCP 도구 목록 ===");
		for (ToolCallback callback : toolCallbacks) {
			log.info("도구 이름: {}", callback.getToolDefinition().name());
			log.info("도구 설명: {}", callback.getToolDefinition().description());
			log.info("---");
		}
		log.info("총 도구 개수: {}", toolCallbacks.length);

		this.chatClient = ChatClient.builder(chatModel)
			.defaultToolCallbacks(toolCallbacks)
			.build();
	}

	//테스트 케이스 전달.
	public String executeTestScenario(String scenario){

		// scenario = """
		// 	mcp를 이용해서 앱테스트를 진행할 예정입니다
		// 	연결된 앱을 찾아서 아래의 동작들을 수행해주세요.
		// 	이미 앱이 켜져있으면 완전히 종료후에 다시 실행 후, 진행하세요.
		//
		//
		// 	[초기 접근시 메인화면이 안나올 경우.]
		// 	1. 디바이스 접근이나 연락처 접근 전부 허용(allow)누르기
		// 	2. 하단의 다음버튼 눌러서 메인화면으로 이동.
		//
		// 	[앱정보]
		// 	이름 : y박스
		//
		// 	[수행할 동작]
		// 	1. 메인화면에 접근.
		// 	2. 중복로그인 팝업이 뜨면 확인버튼을 눌러서 로그인창으로 이동
		// 	3. 출석체크 팝업이 뜨면 로그인버튼을 눌러서 로그인창으로 이동.
		// 	   3-1. 간편로그인 화면이라면, 간편로그인 버튼을 눌러서 로그인 진행.
		// 	4. 로그인시도(id : lsh9672 / pw : 1q2w3e4r@@)
		// 	5. 로그인이 성공하면 yplay 화면으로 이동.
		// 		5-1. Y출석체크 팝업이 뜨면, 클로버 사용하기를 눌러
		// 		5-2. 클로버 이벤트에서 뒤로가기를 토
		// 	6. 'Y소식' 인 이벤트를 찾을때까지 스크롤 해보고, 'Y소식'을 찾으면 클릭
		// 		이미지를 좌우로도 스크롤해보면서 찾기.
		// 	7. 정상적으로 조회되는 것 확인.
		// 	8. 하단 버튼을 눌러서 지정된 링크로 랜딩확인 해당 내용을 수행해줘
		// 		하단 버튼 예) kt멤버십 달달 혜택 바로가기.
		//
		// 	 디바이스 정보는  127.0.0.1:5555 이거야
		// 	""";

		scenario = """
			I will conduct app testing using MCP.
			Please find the connected app and perform the following actions.
			If the app is already running, please completely terminate it and restart before proceeding.
			
			[Initial Access - If Main Screen Doesn't Appear]
			1. Allow all device access or contact access permissions by pressing "Allow"
			2. Press the "Next" button at the bottom to navigate to the main screen
			
			[App Information]
			Name: Y Box
			
			[Actions to Perform]
			1. Access the main screen
			2. If a duplicate login popup appears, press the "Confirm" button to navigate to the login screen
			3. If an attendance check popup appears, press the "Login" button to navigate to the login screen
			4. Attempt login (ID: iwas1006 / PW: 1212qwerd!)
			   4-1. Uncheck the simple login option,
			   4-2. When phone number selection appears, click the next button
			
			5. If login is successful, navigate to the YPlay screen
				5-1. When the Y attendance check popup appears, find the X mark and click it
			6. Keep scrolling until you locate the 'Y News' event, then click on it
			   Also try scrolling horizontally through the images to find it.
			7. Confirm that it displays normally
			8. Press the bottom button to confirm landing on the designated link
			    Bottom button example) kt membership sweet benefits shortcut.
			Device information: 127.0.0.1:5555
			
			""";

		// String systemPrompt = """
		// 	당신은 모바일 앱 테스트 자동화 에이전트입니다.
		//
		// 	주어지는 테스트시나리오대로 앱을 테스트 하고, 결과 보고서를 작성해주세요.
		// 	사용가능한 도구를 이용해서 테스트 시나리오대로 앱을 테스트하면 됩니다.
		//
		//
		// 	[주의사항]
		// 	좌표 기반 접근이 아닌 accessibility ID 사용해서 찾아주세요.
		// 	텍스트로 요소를 찾고, accessibility ID를 이용한 방식입니다.
		//
		// 	아래의 도구는 사용 금지입니다.
		// 	- spring_ai_mcp_client_mobile_mcp_mobile_take_screenshot
		// 	- spring_ai_mcp_client_mobile_mcp_mobile_save_screenshot
		//
		//
		// 	[결과보고서형식]
		// 	1. md 파일 형식일 것.
		// 	2. 테스트 한 내용이 성공인지 실패인지 표로 나타낼것.
		// 	3. md 파일형식에서 표를 제외한 어떠한 값도 넣지 말것.
		// 	""";
		String systemPrompt = """
			You are a mobile app test automation agent.
			
			Please test the app according to the given test scenario and create a results report.
			[Report Format Requirements]
			1. Must be in MD file format
			2. Display test results(success/failure) in table format
			3. The report type should be in MD file format and must only present test content in table format
			4. Do not include any content other than tables in the MD file format
			
			Use the available tools to test the app according to the test scenario.
	
			
			The following tools are prohibited:
			- spring_ai_mcp_client_mobile_mcp_mobile_take_screenshot
			- spring_ai_mcp_client_mobile_mcp_mobile_save_screenshot
			
			[MANDATORY COORDINATE VALIDATION]
			Before any function call, explicitly state:
			"Calling function with x={value}, y={value}" to ensure parameters are valid
			
			- Before calling any coordinate-based function, you MUST verify:
			  1. Both x and y parameters exist
			  2. Both x and y are valid numbers (not undefined, null, or NaN)
			  3. Coordinates are within screen bounds
			- If coordinate validation fails, immediately switch to element-based interaction
			- NEVER attempt coordinate clicks with missing or invalid parameters
			
			[DEBUG INFORMATION]
			Always log which method you're using and why:
			"Using identifier 'com.kt.ydatabox:id/simple_login' because coordinates may be unreliable"
			""";

		try {
			String response = chatClient
				.prompt()
				.system(systemPrompt)
				.user(scenario)
				.call()
				.content();

			log.info("[llm 응답내용] - {}", response);

			return response;

		}
		catch (McpError e){
			if (e.getMessage().contains("Invalid argument")){
				log.warn("좌표 오류 -> " + e.getMessage());
			}
			throw new RuntimeException("mcp 에러 발생.");
		}
		catch (Exception e){
			log.error("Test Execution failed", e);
			throw new RuntimeException("에러 발생.");
		}
	}

}
