package com.crediya.gatewayport;

import com.crediya.model.ApplicationStatus;
import com.crediya.model.LoanApplication;
import reactor.core.publisher.Mono;

public interface ILoanApplicationPersistencePort {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(String id);
    Mono<LoanApplication> findByIdentityDocumentAndStatus(String identityDocument, ApplicationStatus status);
}