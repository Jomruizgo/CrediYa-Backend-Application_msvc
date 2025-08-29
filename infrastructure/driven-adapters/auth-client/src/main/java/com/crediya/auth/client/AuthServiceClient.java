package com.crediya.auth.client;

import com.crediya.auth.dto.UpdateUserDocumentRequestDto;
import com.crediya.auth.dto.UserDto;
import com.crediya.auth.util.AuthApiPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {
    
    private final WebClient webClient;
    
    public AuthServiceClient(WebClient.Builder webClientBuilder, 
                           @Value("${auth.service.url:" + AuthApiPaths.DEFAULT_AUTH_SERVICE_URL + "}") String authServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }
    
    public Mono<UserDto> getUserById(Long userId) {
        return webClient.get()
                .uri(AuthApiPaths.GET_USER_BY_ID, userId)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
    
    public Mono<UserDto> updateUser(Long userId, UpdateUserDocumentRequestDto request) {
        return webClient.put()
                .uri(AuthApiPaths.UPDATE_USER, userId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}