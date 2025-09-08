package com.crediya.auth.filter;

import com.crediya.auth.util.SecurityMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtPropagationFilter implements ExchangeFilterFunction {

    private static final String JWT_TOKEN_KEY = "JWT_TOKEN";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(contextView -> {
            log.debug(SecurityMessages.FILTERING_REQUEST_TO, request.url());
            log.debug(SecurityMessages.AVAILABLE_CONTEXT_KEYS, contextView.stream().map(entry -> entry.getKey().toString()).toList());
            
            if (contextView.hasKey(JWT_TOKEN_KEY)) {
                String token = contextView.get(JWT_TOKEN_KEY);
                ClientRequest.Builder requestBuilder = ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
                
                if (contextView.hasKey("correlationId")) {
                    String correlationId = contextView.get("correlationId");
                    requestBuilder.header("X-Correlation-ID", correlationId);
                }
                
                ClientRequest modifiedRequest = requestBuilder.build();
                
                log.debug(SecurityMessages.PROPAGATING_JWT_TOKEN, 
                         request.url(), token.substring(0, Math.min(10, token.length())));
                return next.exchange(modifiedRequest);
            }
            
            log.warn(SecurityMessages.JWT_TOKEN_NOT_FOUND_IN_CONTEXT, request.url());
            return next.exchange(request);
        });
    }
}