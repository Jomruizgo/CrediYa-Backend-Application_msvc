package com.crediya.api.handler;

import com.crediya.api.mapper.LoanApplicationReviewResponseMapper;
import com.crediya.api.mapper.PageResponseMapper;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.api.util.LogMessages;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.PageFilter;
import com.crediya.usecase.LoanApplicationReviewUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationReviewHandler {

    private final LoanApplicationReviewUseCase loanApplicationReviewUseCase;
    private final LoanApplicationReviewResponseMapper reviewResponseMapper;
    private final PageResponseMapper pageResponseMapper;

    public Mono<ServerResponse> getApplicationsForReview(ServerRequest serverRequest) {
        return CorrelationIdUtil.getCorrelationId()
                .flatMap(correlationId -> {
                    log.info(LogMessages.LOAN_APPLICATION_REVIEW_STARTED, correlationId);
                    
                    return ReactiveSecurityContextHolder.getContext()
                            .flatMap(securityCtx -> {
                                String userRole = securityCtx.getAuthentication().getAuthorities()
                                    .stream()
                                    .findFirst()
                                    .map(authority -> authority.getAuthority())
                                    .orElse("UNKNOWN");
                                
                                PageFilter filter = buildFilterFromRequest(serverRequest);
                                
                                return loanApplicationReviewUseCase.findApplicationsForReview(userRole, filter)
                                        .map(page -> {
                                            var dtoContent = page.getContent().stream()
                                                    .map(reviewResponseMapper::toResponseDto)
                                                    .toList();
                                            return pageResponseMapper.toPageResponseDto(page, dtoContent);
                                        })
                                        .doOnSuccess(response -> 
                                                log.info(LogMessages.LOAN_APPLICATION_REVIEW_SUCCESS, 
                                                        correlationId, response.getContent().size()))
                                        .doOnError(error -> 
                                                log.error(LogMessages.LOAN_APPLICATION_REVIEW_ERROR, 
                                                        correlationId, error))
                                        .flatMap(response -> ServerResponse.ok().bodyValue(response));
                            });
                });
    }

    private PageFilter buildFilterFromRequest(ServerRequest serverRequest) {
        int page = serverRequest.queryParam("page")
                .map(Integer::parseInt)
                .orElse(0);
        int size = serverRequest.queryParam("size")
                .map(Integer::parseInt)
                .orElse(20);
        String sortBy = serverRequest.queryParam("sortBy")
                .orElse("createdAt");
        String sortOrder = serverRequest.queryParam("sortOrder")
                .orElse("DESC");

        List<ApplicationStatus> statuses = Arrays.asList(
                ApplicationStatus.PENDING_REVIEW,
                ApplicationStatus.REJECTED,
                ApplicationStatus.MANUAL_REVIEW
        );

        return new PageFilter.Builder()
                .filter("statuses", statuses)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .page(page, size)
                .build();
    }
}