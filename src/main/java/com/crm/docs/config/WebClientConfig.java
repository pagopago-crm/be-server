package com.crm.docs.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {


	@Value("${api.github.host}")
	private String githubApiHost;

	@Value("${api.github.auth-token}")
	private String githubAuthToken;

	@Value("${api.claude.host}")
	private String claudeApiHost;

	@Value("${api.claude.api-key}")
	private String claudeApiKey;

	// Spring AI 전용 WebClient.Builder (Primary로 지정)
	@Bean
	@Primary
	public RestClient.Builder restClientBuilder() throws Exception {
		// Jetty HttpClient 생성 및 타임아웃 설정
		org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
		httpClient.setConnectTimeout(120000); // 2분
		httpClient.setIdleTimeout(300000); // 5분
		httpClient.start();

		JettyClientHttpRequestFactory requestFactory = new JettyClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(Duration.ofMinutes(5));

		return RestClient.builder()
			.requestFactory(requestFactory);
	}
	// @Bean(name = "springAiWebClientBuilder")
	// public WebClient.Builder webClientBuilder() {
	// 	return WebClient.builder()
	// 		.clientConnector(new ReactorClientHttpConnector(
	// 			reactor.netty.http.client.HttpClient.create()
	// 				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000)
	// 				.responseTimeout(Duration.ofMinutes(5))
	// 		));
	// }

	@Bean(name = "githubWebClient")
	public WebClient githubWebClient(WebClient.Builder builder){

		return builder
			.baseUrl(githubApiHost)
			.defaultHeader("Authorization", "Bearer " + githubAuthToken)
			.defaultHeader("Accept", "application/vnd.github+json")
			.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
			.exchangeStrategies(getExchangeStrategies())
			.clientConnector(createConnector())
			.build();
	}

	@Bean(name = "claudeWebClient")
	public WebClient claudeWebClient(){
		return WebClient.builder()
			.baseUrl(claudeApiHost)
			.defaultHeader("x-api-key", claudeApiKey)
			.defaultHeader("anthropic-version", "2023-06-01")
			.defaultHeader("content-type", "application/json")
			.exchangeStrategies(getExchangeStrategies())
			.clientConnector(createConnector())
			.build();
	}


	//코덱처리를 위한 메모리 크기를 늘려줌.
	private ExchangeStrategies getExchangeStrategies() {

		return ExchangeStrategies.builder()
		.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024*1024*50))
		.build();
	}

	//타임아웃 시간 늘리기.
	private ReactorClientHttpConnector createConnector() {
		return new ReactorClientHttpConnector(
			HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000000) //연결타임아웃 80초 설정(클로드 연결 타임아웃 해결을 위해.)
				.responseTimeout(Duration.ofSeconds(600))
				.wiretap(true)
				.secure(sslContextSpec ->{
					try {
						sslContextSpec.sslContext(
							SslContextBuilder.forClient()
								.trustManager(InsecureTrustManagerFactory.INSTANCE)
								.build()
						);
					} catch (SSLException e) {
						throw new RuntimeException(e);
					}
				}).resolver(DefaultAddressResolverGroup.INSTANCE)
		);

	}
}
