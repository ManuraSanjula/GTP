package com.gtpuser.gtpuser.security.support;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gtpuser.gtpuser.utils.TokenManager;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

	private static final String BEARER = "Bearer ";
	private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
	private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono
			.justOrEmpty(authValue.substring(BEARER.length()));
	
	private final TokenManager tokenConverter;

	public ServerHttpBearerAuthenticationConverter(TokenManager tokenConverter) {
		super();
		this.tokenConverter = tokenConverter;
	}

	@Override
	public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange).flatMap(ServerHttpBearerAuthenticationConverter::extract)
				.filter(matchBearerLength).flatMap(isolateBearerValue).flatMap(user -> {
					return tokenConverter.validateTokenSignature(user).map(u -> {
						return Mono.justOrEmpty(
								new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
					}).flatMap(i -> i);
				});
	}

	public static Mono<String> extract(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
	}
}
