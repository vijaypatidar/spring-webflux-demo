package com.vkpapps.demo.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationFilter implements WebFilter {

  public static final String HEADER_PREFIX = "Bearer ";

  private final JwtTokenProvider tokenProvider;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String token = resolveToken(exchange.getRequest());
    if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
      var authentication = this.tokenProvider.getAuthentication(token);
      log.info("Username:" + authentication.getName()
          + " is authenticated successfully. Requested resource uir:"
          + exchange.getRequest().getPath() + " Method:" + exchange.getRequest().getMethodValue());
      return chain.filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
    return chain.filter(exchange);
  }

  private String resolveToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

}