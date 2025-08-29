package com.crediya.api.handler;

import com.crediya.api.docs.LoanTypeApiDocs;
import com.crediya.api.util.LogMessages;
import com.crediya.serviceport.ILoanTypeServicePort;
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
@Tag(name = "Loan Types", description = "Operations related to loan types")
public class LoanTypeHandler extends LoanTypeApiDocs {

    private final ILoanTypeServicePort loanTypeServicePort;

    public Mono<ServerResponse> getAllActiveLoanTypes(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        log.info(LogMessages.LOAN_TYPE_GET_ALL_STARTED, correlationId);
        return loanTypeServicePort.getAllActiveLoanTypes()
                .collectList()
                .doOnSuccess(loanTypes -> log.info(LogMessages.LOAN_TYPE_GET_ALL_SUCCESS, correlationId, loanTypes.size()))
                .doOnError(error -> log.error(LogMessages.LOAN_TYPE_GET_ALL_ERROR, correlationId, error))
                .flatMap(loanTypes -> ServerResponse.ok().bodyValue(loanTypes))
                .contextWrite(ctx -> ctx.put("correlationId", correlationId));
    }

    public Mono<ServerResponse> getLoanTypeById(ServerRequest serverRequest) {
        String correlationId = getCorrelationId(serverRequest);
        Long loanTypeId = Long.valueOf(serverRequest.pathVariable("id"));
        log.info(LogMessages.LOAN_TYPE_GET_BY_ID_STARTED, correlationId, loanTypeId);
        return loanTypeServicePort.getLoanTypeById(loanTypeId)
                .doOnSuccess(loanType -> log.info(LogMessages.LOAN_TYPE_GET_BY_ID_SUCCESS, correlationId, loanTypeId))
                .doOnError(error -> log.error(LogMessages.LOAN_TYPE_GET_BY_ID_ERROR, correlationId, loanTypeId, error))
                .flatMap(loanType -> ServerResponse.ok().bodyValue(loanType))
                .onErrorResume(Exception.class, error -> {
                    log.error(LogMessages.LOAN_TYPE_GET_BY_ID_ERROR, correlationId, loanTypeId, error);
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