package com.crediya.usecase;

import com.crediya.model.LoanApplication;
import com.crediya.model.Loan;
import com.crediya.model.ApplicationStatus;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.exception.LoanApplicationAlreadyExistsException;
import com.crediya.exception.UnauthorizedUserException;
import com.crediya.serviceport.ILoanApplication;
import com.crediya.util.Constant;
import com.crediya.util.UseCaseMessages;
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

    public Mono<LoanApplication> execute(Long userId, String userRole, String identityDocument, Loan loan) {
        return validateUserRole(userRole)
                .then(validateInput(identityDocument, loan))
                .then(validateLoanLimits(loan))
                .then(validateNoPendingApplication(identityDocument))
                .then(authCommunicationPort.validateAndUpdateUserDocument(userId, identityDocument))
                .then(createLoanApplication(identityDocument, loan))
                .flatMap(loanApplicationPersistencePort::save);
    }

    private Mono<Void> validateUserRole(String userRole) {
        if (!UseCaseMessages.CLIENT_ROLE.equals(userRole)) {
            return Mono.error(new UnauthorizedUserException(
                String.format(UseCaseMessages.UNAUTHORIZED_USER_ROLE, userRole)));
        }
        return Mono.empty();
    }

    private Mono<Void> validateInput(String identityDocument, Loan loan) {
        if (identityDocument == null || identityDocument.trim().isEmpty()) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.IDENTITY_DOCUMENT_REQUIRED));
        }
        if (loan == null) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.LOAN_TYPE_REQUIRED));
        }
        if (loan.getAmount() == null) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.AMOUNT_REQUIRED));
        }
        if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.AMOUNT_MUST_BE_POSITIVE));
        }
        if (loan.getTermMonths() == null) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.TERM_REQUIRED));
        }
        if (loan.getTermMonths() <= 0) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.TERM_MUST_BE_POSITIVE));
        }
        if (loan.getLoanTypeId() == null) {
            return Mono.error(new InvalidLoanApplicationDataException(UseCaseMessages.LOAN_TYPE_REQUIRED));
        }
        return Mono.empty();
    }


    private Mono<Void> validateLoanLimits(Loan loan) {
        return loanTypePersistencePort.findByIdAndActive(loan.getLoanTypeId())
                .switchIfEmpty(Mono.error(new InvalidLoanApplicationDataException(
                    String.format(UseCaseMessages.INVALID_LOAN_TYPE, loan.getLoanTypeId()))))
                .flatMap(loanType -> {
                    if (loan.getAmount().compareTo(loanType.getMinAmount()) < 0) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format(UseCaseMessages.AMOUNT_BELOW_MINIMUM, 
                                loan.getAmount(), loanType.getMinAmount(), loanType.getName())));
                    }
                    if (loan.getAmount().compareTo(loanType.getMaxAmount()) > 0) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format(UseCaseMessages.AMOUNT_EXCEEDS_MAXIMUM, 
                                loan.getAmount(), loanType.getMaxAmount(), loanType.getName())));
                    }
                    if (loan.getTermMonths() < loanType.getMinTermMonths()) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format(UseCaseMessages.TERM_BELOW_MINIMUM, 
                                loan.getTermMonths(), loanType.getMinTermMonths(), loanType.getName())));
                    }
                    if (loan.getTermMonths() > loanType.getMaxTermMonths()) {
                        return Mono.error(new InvalidLoanApplicationDataException(
                            String.format(UseCaseMessages.TERM_EXCEEDS_MAXIMUM, 
                                loan.getTermMonths(), loanType.getMaxTermMonths(), loanType.getName())));
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> validateNoPendingApplication(String identityDocument) {
        return loanApplicationPersistencePort.findByIdentityDocumentAndStatus(identityDocument, ApplicationStatus.PENDING_REVIEW)
                .flatMap(existingApplication -> {
                    return Mono.<Void>error(new LoanApplicationAlreadyExistsException(UseCaseMessages.LOAN_APPLICATION_ALREADY_EXISTS));
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<LoanApplication> findById(String id) {
        return loanApplicationPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new InvalidLoanApplicationDataException(
                    String.format(UseCaseMessages.LOAN_APPLICATION_NOT_FOUND, id))));
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