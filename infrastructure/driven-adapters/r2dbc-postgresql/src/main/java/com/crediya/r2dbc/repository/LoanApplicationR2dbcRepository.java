package com.crediya.r2dbc.repository;

import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanApplicationR2dbcRepository extends ReactiveCrudRepository<LoanApplicationEntity, String> {
    Mono<LoanApplicationEntity> findByIdentityDocumentAndStatus(String identityDocument, String status);
}