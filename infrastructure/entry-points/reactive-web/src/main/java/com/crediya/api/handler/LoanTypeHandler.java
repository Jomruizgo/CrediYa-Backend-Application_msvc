package com.crediya.api.handler;

import com.crediya.api.docs.LoanTypeApiDocs;
import com.crediya.api.util.LogMessages;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.serviceport.ILoanTypeServicePort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "Loan Types", description = "Operations related to loan types")
public class LoanTypeHandler extends LoanTypeApiDocs {

    private final ILoanTypeServicePort loanTypeServicePort;

    public Mono<ServerResponse> getAllActiveLoanTypes(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(LogMessages.LOAN_TYPE_GET_ALL_STARTED, correlationId);
                    
                    return loanTypeServicePort.getAllActiveLoanTypes()
                            .collectList()
                            .doOnSuccess(loanTypes -> log.info(LogMessages.LOAN_TYPE_GET_ALL_SUCCESS, correlationId, loanTypes.size()))
                            .doOnError(error -> log.error(LogMessages.LOAN_TYPE_GET_ALL_ERROR, correlationId, error))
                            .flatMap(loanTypes -> ServerResponse.ok().bodyValue(loanTypes));
                });
    }

    public Mono<ServerResponse> getLoanTypeById(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    Long loanTypeId = Long.valueOf(serverRequest.pathVariable("id"));
                    log.info(LogMessages.LOAN_TYPE_GET_BY_ID_STARTED, correlationId, loanTypeId);
                    
                    return loanTypeServicePort.getLoanTypeById(loanTypeId)
                            .doOnSuccess(loanType -> log.info(LogMessages.LOAN_TYPE_GET_BY_ID_SUCCESS, correlationId, loanTypeId))
                            .doOnError(error -> log.error(LogMessages.LOAN_TYPE_GET_BY_ID_ERROR, correlationId, loanTypeId, error))
                            .flatMap(loanType -> ServerResponse.ok().bodyValue(loanType))
                            .onErrorResume(Exception.class, error -> {
                                log.error(LogMessages.LOAN_TYPE_GET_BY_ID_ERROR, correlationId, loanTypeId, error);
                                return ServerResponse.notFound().build();
                            });
                });
    }
}