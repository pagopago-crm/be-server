package com.crm.docs.common.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
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

	@Bean(name = "githubWebClient")
	public WebClient githubWebClient(WebClient.Builder builder){

		return builder
			.baseUrl(githubApiHost)
			.defaultHeader("Authorization", githubAuthToken)
			.defaultHeader("Accept", "application/vnd.github+json")
			.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
			.exchangeStrategies(getExchangeStrategies())
			.clientConnector(createConnector())
			.build();
	}

	@Bean(name = "claudeWebClient")
	public WebClient claudeWebClient(WebClient.Builder builder){
		return builder
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
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 800000) //연결타임아웃 80초 설정(클로드 연결 타임아웃 해결을 위해.)
				.responseTimeout(Duration.ofSeconds(60))
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
				})
		);

	}
}
