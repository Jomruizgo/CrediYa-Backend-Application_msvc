package com.crediya.api.handler;

import com.crediya.api.docs.LoanApplicationApiDocs;
import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.mapper.LoanApplicationRequestMapper;
import com.crediya.api.mapper.LoanApplicationResponseMapper;
import com.crediya.api.util.LogMessages;
import com.crediya.usecase.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Loan Applications", description = "Operations related to loan applications")
public class LoanApplicationHandler extends LoanApplicationApiDocs {

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
                    loanApplicationUseCase.execute(request.getUserId(), request.getIdentityDocument(), requestMapper.toLoan(request)))
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
        return loanApplicationUseCase.findById(applicationId)
                .map(responseMapper::toResponseDto)
                .doOnSuccess(response -> log.info(LogMessages.LOAN_APPLICATION_SEARCH_BY_ID_SUCCESS, correlationId, applicationId))
                .doOnError(error -> log.error(LogMessages.LOAN_APPLICATION_SEARCH_BY_ID_ERROR, correlationId, applicationId, error))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(Exception.class, error -> {
                    log.error(LogMessages.LOAN_APPLICATION_SEARCH_BY_ID_ERROR, correlationId, applicationId, error);
                    return ServerResponse.notFound().build();
                })
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }
    
    private String getCorrelationId(ServerRequest request) {
        String correlationId = request.headers().firstHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        return correlationId;
    }
}
