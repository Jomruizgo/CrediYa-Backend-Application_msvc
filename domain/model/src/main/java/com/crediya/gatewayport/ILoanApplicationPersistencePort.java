package com.crediya.gatewayport;

import com.crediya.model.ApplicationStatus;
import com.crediya.model.LoanApplication;
import com.crediya.model.Page;
import com.crediya.model.PageFilter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface ILoanApplicationPersistencePort {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(String id);
    Mono<LoanApplication> findByIdentityDocumentAndStatus(String identityDocument, ApplicationStatus status);
    Mono<Page<LoanApplication>> findApplicationsForReview(PageFilter filter);
    Flux<LoanApplication> findApprovedApplicationsByIdentityDocument(String identityDocument);
}