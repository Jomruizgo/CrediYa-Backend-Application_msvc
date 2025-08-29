package com.crediya.gatewayport;

import com.crediya.model.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanTypePersistencePort {
    
    Mono<LoanType> findById(Long id);
    Flux<LoanType> findAllActive();
    Mono<LoanType> findByIdAndActive(Long id);
    Mono<LoanType> save(LoanType loanType);
    Mono<LoanType> update(LoanType loanType);
    Mono<Void> deleteById(Long id);
}