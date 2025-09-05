package com.crediya.api.handler;

import com.crediya.api.docs.LoanApplicationApiDocs;
import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.mapper.LoanApplicationRequestMapper;
import com.crediya.api.mapper.LoanApplicationResponseMapper;
import com.crediya.api.util.LogMessages;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.usecase.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Operations related to loan applications")
public class LoanApplicationHandler extends LoanApplicationApiDocs {

    private final LoanApplicationUseCase loanApplicationUseCase;
    private final LoanApplicationRequestMapper requestMapper;
    private final LoanApplicationResponseMapper responseMapper;

    public Mono<ServerResponse> registerApplication(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(LogMessages.LOAN_APPLICATION_REGISTER_STARTED, correlationId);
                    
                    return ReactiveSecurityContextHolder.getContext()
                            .flatMap(securityCtx -> {
                                Long userId = Long.valueOf((String) securityCtx.getAuthentication().getPrincipal());
                                String userRole = securityCtx.getAuthentication().getAuthorities()
                                    .stream()
                                    .findFirst()
                                    .map(authority -> authority.getAuthority())
                                    .orElse("UNKNOWN");
                                
                                return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                                    .doOnNext(request -> 
                                        log.debug(LogMessages.LOAN_APPLICATION_REGISTER_DATA_RECEIVED, correlationId, request.getIdentityDocument()))
                                    .flatMap(request -> 
                                        loanApplicationUseCase.execute(userId, userRole, request.getIdentityDocument(), requestMapper.toLoan(request)))
                                    .map(responseMapper::toResponseDto)
                                    .doOnSuccess(response -> log.info(LogMessages.LOAN_APPLICATION_REGISTER_SUCCESS, correlationId, response.getId()))
                                    .doOnError(error -> log.error(LogMessages.LOAN_APPLICATION_REGISTER_ERROR, correlationId, error))
                                    .flatMap(response -> ServerResponse.ok().bodyValue(response));
                            });
                });
    }

    public Mono<ServerResponse> getApplicationById(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
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
                            });
                });
    }
}
