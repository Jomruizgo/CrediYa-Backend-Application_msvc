package com.crediya.api.config.security;

import com.crediya.api.util.SecurityMessages;
import com.crediya.gatewayport.ITokenValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {
    
    private final ITokenValidationPort tokenValidationPort;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        String path = exchange.getRequest().getPath().value();
        
        if (token == null) {
            log.warn(SecurityMessages.NO_JWT_TOKEN_FOUND, correlationId, path);
            return chain.filter(exchange);
        }
        
        if (!tokenValidationPort.validateToken(token)) {
            log.warn(SecurityMessages.INVALID_JWT_TOKEN, correlationId, path);
            return chain.filter(exchange);
        }
        
        return authenticateToken(token, exchange)
                .flatMap(authentication -> {
                    log.debug(SecurityMessages.SAVING_TOKEN_IN_CONTEXT, token.substring(0, Math.min(10, token.length())));
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                            .contextWrite(ctx -> ctx.put("JWT_TOKEN", token));
                })
                .onErrorResume(error -> {
                    log.debug(SecurityMessages.AUTHENTICATION_FAILED_FOR_TOKEN, error);
                    return chain.filter(exchange);
                });
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(SecurityMessages.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityMessages.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Mono<UsernamePasswordAuthenticationToken> authenticateToken(String token, ServerWebExchange exchange) {
        String userId = tokenValidationPort.extractUserId(token);
        String role = tokenValidationPort.extractRole(token);
        
        if (userId != null && role != null) {
            String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
            try {
                Long.valueOf(userId);
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(SecurityMessages.ROLE_PREFIX + role)
                );
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                return Mono.just(authentication)
                        .contextWrite(ctx -> ctx.put("correlationId", correlationId));
            } catch (NumberFormatException e) {
                log.debug(SecurityMessages.INVALID_USER_ID_FORMAT, userId);
                return Mono.empty();
            }
        }
        return Mono.empty();
    }
}