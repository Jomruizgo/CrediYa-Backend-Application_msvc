package com.crediya.serviceport;

import com.crediya.model.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanTypeServicePort {
    
    Mono<LoanType> getLoanTypeById(Long id);
    
    Flux<LoanType> getAllActiveLoanTypes();
    
    Mono<LoanType> createLoanType(LoanType loanType);
    
    Mono<LoanType> updateLoanType(Long id, LoanType loanType);
    
    Mono<Void> deleteLoanType(Long id);
}