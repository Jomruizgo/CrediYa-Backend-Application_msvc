package com.crediya.serviceport;

import com.crediya.model.Loan;
import reactor.core.publisher.Mono;

public interface ILoanApplication {

    Mono<com.crediya.model.LoanApplication> execute(Long userId, String userRole, String identityDocument, Loan loan);
    
    Mono<com.crediya.model.LoanApplication> findById(String id);

}
