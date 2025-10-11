package com.crm.docs.common.config.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VncProxyHandler extends BinaryWebSocketHandler {

	private final Map<String, WebSocketSession> vncSessions = new ConcurrentHashMap<>();
	private final StandardWebSocketClient webSocketClient;

	public VncProxyHandler() {
		// WebSocket 클라이언트에 버퍼 크기 설정
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxBinaryMessageBufferSize(64 * 1024);  // 64KB
		container.setDefaultMaxTextMessageBufferSize(64 * 1024);    // 64KB

		this.webSocketClient = new StandardWebSocketClient(container);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession browserSession) throws Exception {
		log.info("브라우저 연결됨: {}", browserSession.getId());

		String vncWsUrl = "ws://localhost:6080/";

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
}