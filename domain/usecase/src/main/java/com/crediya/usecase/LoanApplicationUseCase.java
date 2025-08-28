package com.crediya.usecase;

import com.crediya.model.LoanApplication;
import com.crediya.model.Loan;
import com.crediya.model.ApplicationStatus;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.exception.LoanApplicationException;
import com.crediya.serviceport.ILoanApplication;
import com.crediya.util.Constant;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoanApplicationUseCase implements ILoanApplication {
    
    private final ILoanApplicationPersistencePort loanApplicationPersistencePort;

    public LoanApplicationUseCase(ILoanApplicationPersistencePort loanApplicationPersistencePort) {
        this.loanApplicationPersistencePort = loanApplicationPersistencePort;
    }

    public Mono<LoanApplication> execute(String identityDocument, Loan loan) {
        return validateInput(identityDocument, loan)
                .then(createLoanApplication(identityDocument, loan))
                .flatMap(loanApplicationPersistencePort::save);
    }

    private Mono<Void> validateInput(String identityDocument, Loan loan) {
        return Mono.fromRunnable(() -> {
            if (identityDocument == null || identityDocument.trim().isEmpty()) {
                throw new LoanApplicationException(Constant.IDENTITY_DOCUMENT_REQUIRED);
            }
            if (loan == null) {
                throw new LoanApplicationException(Constant.LOAN_TYPE_REQUIRED);
            }
            if (loan.getAmount() == null) {
                throw new LoanApplicationException(Constant.AMOUNT_REQUIRED);
            }
            if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new LoanApplicationException(Constant.AMOUNT_MUST_BE_POSITIVE);
            }
            if (loan.getTermMonths() == null) {
                throw new LoanApplicationException(Constant.TERM_REQUIRED);
            }
            if (loan.getTermMonths() <= 0) {
                throw new LoanApplicationException(Constant.TERM_MUST_BE_POSITIVE);
            }
            if (loan.getType() == null) {
                throw new LoanApplicationException(Constant.LOAN_TYPE_REQUIRED);
            }
        });
    }

    private Mono<LoanApplication> createLoanApplication(String identityDocument, Loan loan) {
        return Mono.fromSupplier(() -> new LoanApplication(
                UUID.randomUUID().toString(),
                identityDocument,
                loan,
                ApplicationStatus.PENDING_REVIEW,
                LocalDateTime.now()
        ));
    }
}