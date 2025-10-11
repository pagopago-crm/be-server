package com.crm.docs.config.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.crm.docs.common.util.DeviceServiceMapper;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VncProxyHandler extends BinaryWebSocketHandler {

	private final Map<String, WebSocketSession> vncSessions = new ConcurrentHashMap<>();
	private final StandardWebSocketClient webSocketClient;
	private final DeviceServiceMapper deviceServiceMapper;

	@Value("${spring.profiles.active:local}")
	private String activeProfile;

	public VncProxyHandler(DeviceServiceMapper deviceServiceMapper) {
		// WebSocket 클라이언트에 버퍼 크기 설정
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxBinaryMessageBufferSize(64 * 1024);  // 64KB
		container.setDefaultMaxTextMessageBufferSize(64 * 1024);    // 64KB

		this.webSocketClient = new StandardWebSocketClient(container);
		this.deviceServiceMapper = deviceServiceMapper;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession browserSession) throws Exception {
		log.info("브라우저 연결됨: {}", browserSession.getId());

		URI uri = browserSession.getUri();
		String path = uri.getPath();

		// /websockify/{osTp}/{deviceType}/{osVersion} 파싱
		String[] pathSegments = path.split("/");

		if (pathSegments.length < 5) {
			log.error("잘못된 WebSocket 경로: {}", path);
			browserSession.close(CloseStatus.BAD_DATA);
			return;
		}

		String os = pathSegments[2];           // osTp
		String device = pathSegments[3];        // deviceType
		String osVersion = pathSegments[4];    // osVersion

		String vncWsUrl = getVncWsUrl(os, device, osVersion);
		log.info("VNC 연결 시도: {}", vncWsUrl);

		// VNC websockify 서버로 WebSocket 연결
		webSocketClient.execute(
			new AbstractWebSocketHandler() {

				@Override
				public void afterConnectionEstablished(WebSocketSession vncSession) throws Exception {
					vncSessions.put(browserSession.getId(), vncSession);
					log.info("VNC 연결 성공: {}", browserSession.getId());
				}

				@Override
				protected void handleBinaryMessage(WebSocketSession vncSession, BinaryMessage message) throws Exception {
					if (browserSession.isOpen()) {
						browserSession.sendMessage(message);
					}
				}

				@Override
				protected void handleTextMessage(WebSocketSession vncSession, TextMessage message) {
					try {
						if (browserSession.isOpen()) {
							browserSession.sendMessage(message);
						}
					} catch (IOException e) {
						log.error("브라우저로 메시지 전송 실패", e);
					}
				}

				@Override
				public void handleTransportError(WebSocketSession vncSession, Throwable exception) throws Exception {
					log.error("VNC 전송 에러: {}", exception.getMessage());
				}

				@Override
				public void afterConnectionClosed(WebSocketSession vncSession, CloseStatus status) throws Exception {
					log.info("VNC 연결 종료: {}", status);
					if (browserSession.isOpen()) {
						browserSession.close();
					}
					vncSessions.remove(browserSession.getId());
				}
			},
			vncWsUrl
		);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession browserSession, BinaryMessage message) throws Exception {
		WebSocketSession vncSession = vncSessions.get(browserSession.getId());
		if (vncSession != null && vncSession.isOpen()) {
			vncSession.sendMessage(message);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession browserSession, TextMessage message) {
		try {
			WebSocketSession vncSession = vncSessions.get(browserSession.getId());
			if (vncSession != null && vncSession.isOpen()) {
				vncSession.sendMessage(message);
			}
		} catch (IOException e) {
			log.error("VNC로 메시지 전송 실패", e);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession browserSession, CloseStatus status) throws Exception {
		log.info("브라우저 연결 종료: {}", status);
		WebSocketSession vncSession = vncSessions.remove(browserSession.getId());
		if (vncSession != null && vncSession.isOpen()) {
			vncSession.close();
		}
	}


	/**
	 * 환경에 따라 VNC WebSocket URL 생성
	 */
	private String getVncWsUrl(String os, String device, String osVersion) {
		if ("local".equals(activeProfile)) {
			return "ws://localhost:6080/";
		}

		// dev 환경: K8s service 이름 사용
		String serviceName = deviceServiceMapper.getServiceName(os, device, osVersion);
		return String.format("ws://%s:6080/", serviceName);
	}

	/**
	 * 쿼리 파라미터 파싱
	 */
	private Map<String, String> parseQueryParams(String query) {
		Map<String, String> params = new ConcurrentHashMap<>();
		if (query == null || query.isEmpty()) {
			return params;
		}

		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			if (pair.length == 2) {
				params.put(pair[0], pair[1]);
			}
		}
		return params;
	}
}