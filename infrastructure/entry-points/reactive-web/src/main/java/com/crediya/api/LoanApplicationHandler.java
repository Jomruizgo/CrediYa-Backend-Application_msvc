package com.crediya.api;

import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.mapper.LoanApplicationRequestMapper;
import com.crediya.api.mapper.LoanApplicationResponseMapper;
import com.crediya.api.util.LogMessages;
import com.crediya.usecase.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final LoanApplicationUseCase loanApplicationUseCase;
    private final LoanApplicationRequestMapper requestMapper;
    private final LoanApplicationResponseMapper responseMapper;

    public Mono<ServerResponse> registerApplication(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        log.info(LogMessages.LOAN_APPLICATION_REGISTER_STARTED, correlationId);
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .doOnNext(request -> 
                    log.debug(LogMessages.LOAN_APPLICATION_REGISTER_DATA_RECEIVED, correlationId, request.getIdentityDocument()))
                .flatMap(request -> 
                    loanApplicationUseCase.execute(request.getIdentityDocument(), requestMapper.toLoan(request)))
                .map(responseMapper::toResponseDto)
                .doOnSuccess(response -> log.info(LogMessages.LOAN_APPLICATION_REGISTER_SUCCESS, correlationId, response.getId()))
                .doOnError(error -> log.error(LogMessages.LOAN_APPLICATION_REGISTER_ERROR, correlationId, error))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> getApplicationById(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        String applicationId = serverRequest.pathVariable("id");
        log.info(LogMessages.LOAN_APPLICATION_SEARCH_BY_ID_STARTED, correlationId, applicationId);
        // TODO: Implementar cuando tengamos findById en el usecase
        return ServerResponse.notFound().build();
    }
    
    private String getCorrelationId(ServerRequest request) {
        String correlationId = request.headers().firstHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        return correlationId;
    }
}
