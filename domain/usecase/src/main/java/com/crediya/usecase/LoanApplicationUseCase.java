package com.crediya.usecase;

import com.crediya.model.LoanApplication;
import com.crediya.model.Loan;
import com.crediya.model.ApplicationStatus;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.exception.LoanApplicationAlreadyExistsException;
import com.crediya.serviceport.ILoanApplication;
import com.crediya.util.Constant;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoanApplicationUseCase implements ILoanApplication {
    
    private final ILoanApplicationPersistencePort loanApplicationPersistencePort;
    private final IAuthCommunicationPort authCommunicationPort;
    private final ILoanTypePersistencePort loanTypePersistencePort;

    public LoanApplicationUseCase(ILoanApplicationPersistencePort loanApplicationPersistencePort,
                                IAuthCommunicationPort authCommunicationPort,
                                ILoanTypePersistencePort loanTypePersistencePort) {
        this.loanApplicationPersistencePort = loanApplicationPersistencePort;
        this.authCommunicationPort = authCommunicationPort;
        this.loanTypePersistencePort = loanTypePersistencePort;
    }

    public Mono<LoanApplication> execute(Long userId, String identityDocument, Loan loan) {
        return validateInput(identityDocument, loan)
                .then(validateLoanLimits(loan))
                .then(validateNoPendingApplication(identityDocument))
                .then(authCommunicationPort.validateAndUpdateUserDocument(userId, identityDocument))
                .then(createLoanApplication(identityDocument, loan))
                .flatMap(loanApplicationPersistencePort::save);
    }

    private Mono<Void> validateInput(String identityDocument, Loan loan) {
        return Mono.fromRunnable(() -> {
            if (identityDocument == null || identityDocument.trim().isEmpty()) {
                throw new InvalidLoanApplicationDataException(Constant.IDENTITY_DOCUMENT_REQUIRED);
            }
            if (loan == null) {
                throw new InvalidLoanApplicationDataException(Constant.LOAN_TYPE_REQUIRED);
            }
            if (loan.getAmount() == null) {
                throw new InvalidLoanApplicationDataException(Constant.AMOUNT_REQUIRED);
            }
            if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidLoanApplicationDataException(Constant.AMOUNT_MUST_BE_POSITIVE);
            }
            if (loan.getTermMonths() == null) {
                throw new InvalidLoanApplicationDataException(Constant.TERM_REQUIRED);
            }
            if (loan.getTermMonths() <= 0) {
                throw new InvalidLoanApplicationDataException(Constant.TERM_MUST_BE_POSITIVE);
            }
            if (loan.getLoanTypeId() == null) {
                throw new InvalidLoanApplicationDataException(Constant.LOAN_TYPE_REQUIRED);
            }
        });
    }

    private Mono<Void> validateLoanLimits(Loan loan) {
        return loanTypePersistencePort.findByIdAndActive(loan.getLoanTypeId())
                .switchIfEmpty(Mono.error(new InvalidLoanApplicationDataException("Invalid loan type ID: " + loan.getLoanTypeId())))
                .flatMap(loanType -> {
                    if (loan.getAmount().compareTo(loanType.getMinAmount()) < 0) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format("Amount %s is below minimum %s for loan type %s", 
                                loan.getAmount(), loanType.getMinAmount(), loanType.getName())));
                    }
                    if (loan.getAmount().compareTo(loanType.getMaxAmount()) > 0) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format("Amount %s exceeds maximum %s for loan type %s", 
                                loan.getAmount(), loanType.getMaxAmount(), loanType.getName())));
                    }
                    if (loan.getTermMonths() < loanType.getMinTermMonths()) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format("Term %s months is below minimum %s for loan type %s", 
                                loan.getTermMonths(), loanType.getMinTermMonths(), loanType.getName())));
                    }
                    if (loan.getTermMonths() > loanType.getMaxTermMonths()) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format("Term %s months exceeds maximum %s for loan type %s", 
                                loan.getTermMonths(), loanType.getMaxTermMonths(), loanType.getName())));
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> validateNoPendingApplication(String identityDocument) {
        return loanApplicationPersistencePort.findByIdentityDocumentAndStatus(identityDocument, ApplicationStatus.PENDING_REVIEW)
                .flatMap(existingApplication -> {
                    return Mono.<Void>error(new LoanApplicationAlreadyExistsException(identityDocument));
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<LoanApplication> findById(String id) {
        return loanApplicationPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new InvalidLoanApplicationDataException("Loan application not found with id: " + id)));
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