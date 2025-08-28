package com.crediya.api;

import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.mapper.LoanApplicationRequestMapper;
import com.crediya.api.mapper.LoanApplicationResponseMapper;
import com.crediya.usecase.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationHandler.class);
    private final LoanApplicationUseCase loanApplicationUseCase;
    private final LoanApplicationRequestMapper requestMapper;
    private final LoanApplicationResponseMapper responseMapper;

    public Mono<ServerResponse> registerApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .doOnNext(request -> 
                    logger.info("Received loan application request for document: {}", request.getIdentityDocument()))
                .flatMap(request -> 
                    loanApplicationUseCase.execute(request.getIdentityDocument(), requestMapper.toLoan(request)))
                .map(responseMapper::toResponseDto)
                .flatMap(response -> {
                    logger.info("Loan application processed successfully with ID: {}", response.getId());
                    return ServerResponse.ok().bodyValue(response);
                })
                .onErrorResume(throwable -> {
                    logger.error("Error processing loan application request", throwable);
                    return ServerResponse.badRequest().bodyValue(throwable.getMessage());
                });
    }

    public Mono<ServerResponse> getApplicationById(ServerRequest serverRequest) {
        // Implementar cuando tengamos findById en el usecase
        String applicationId = serverRequest.pathVariable("id");
        logger.info("Getting loan application with ID: {}", applicationId);
        return ServerResponse.notFound().build();
    }
}
