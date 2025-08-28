package com.crediya.serviceport;

import com.crediya.model.Loan;
import reactor.core.publisher.Mono;

public interface ILoanApplication {

    public Mono<com.crediya.model.LoanApplication> execute(String identityDocument, Loan loan);


}
