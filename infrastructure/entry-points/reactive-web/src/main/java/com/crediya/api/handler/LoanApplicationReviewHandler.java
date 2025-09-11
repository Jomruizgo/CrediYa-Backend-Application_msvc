package com.crediya.api.handler;

import com.crediya.api.docs.LoanApplicationReviewApiDocs;
import com.crediya.api.mapper.LoanApplicationReviewResponseMapper;
import com.crediya.api.mapper.PageResponseMapper;
import com.crediya.api.util.CorrelationIdUtil;
import com.crediya.api.util.LogMessages;
import com.crediya.api.util.SecurityMessages;
import com.crediya.exception.UnauthorizedUserException;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.PageFilter;
import com.crediya.serviceport.ILoanApplicationReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationReviewHandler extends LoanApplicationReviewApiDocs {

    private final ILoanApplicationReviewService loanApplicationReviewService;
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
                                    .map(authority -> {
                                        String role = authority.getAuthority();
                                        return role.startsWith("ROLE_") ? role.substring(5) : role;
                                    })
                                    .orElse("UNKNOWN");
                                
                                return buildFilterFromRequest(serverRequest)
                                        .flatMap(filter -> loanApplicationReviewService.findApplicationsForReview(userRole, filter))
                                        .flatMap(page -> 
                                            Flux.fromIterable(page.getContent())
                                                    .map(reviewResponseMapper::toResponseDto)
                                                    .collectList()
                                                    .map(dtoContent -> pageResponseMapper.toPageResponseDto(page, dtoContent))
                                        )
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

    private Mono<PageFilter> buildFilterFromRequest(ServerRequest serverRequest) {
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

        // Parse status filter from query params reactively
        return parseStatusesFromRequest(serverRequest)
                .map(statuses -> new PageFilter.Builder()
                        .filter("statuses", statuses)
                        .sortBy(sortBy)
                        .sortOrder(sortOrder)
                        .page(page, size)
                        .build());
    }

    private Mono<List<ApplicationStatus>> parseStatusesFromRequest(ServerRequest serverRequest) {
        return serverRequest.queryParam("status")
                .or(() -> serverRequest.queryParam("statuses"))
                .map(statusParam -> {
                    try {
                        String[] statusArray = statusParam.split(",");
                        List<ApplicationStatus> statuses = Arrays.stream(statusArray)
                                .map(String::trim)
                                .map(String::toUpperCase)
                                .map(ApplicationStatus::valueOf)
                                .toList();
                        return Mono.just(statuses);
                    } catch (IllegalArgumentException e) {
                        return CorrelationIdUtil.getCorrelationId()
                                .doOnNext(correlationId -> 
                                    log.warn(LogMessages.LOAN_APPLICATION_REVIEW_INVALID_STATUS, correlationId, statusParam))
                                .then(Mono.just(getDefaultStatuses()));
                    }
                })
                .orElse(Mono.just(getDefaultStatuses()));
    }

    private List<ApplicationStatus> getDefaultStatuses() {
        return Arrays.asList(
                ApplicationStatus.PENDING_REVIEW,
                ApplicationStatus.REJECTED,
                ApplicationStatus.MANUAL_REVIEW
        );
    }
}