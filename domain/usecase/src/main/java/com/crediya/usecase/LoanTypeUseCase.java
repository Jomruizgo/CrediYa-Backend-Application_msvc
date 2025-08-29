package com.crediya.usecase;

import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.LoanType;
import com.crediya.serviceport.ILoanTypeServicePort;
import com.crediya.util.LoanTypeErrorMessages;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LoanTypeUseCase implements ILoanTypeServicePort {

    private final ILoanTypePersistencePort loanTypePersistencePort;
    
    public LoanTypeUseCase(ILoanTypePersistencePort loanTypePersistencePort) {
        this.loanTypePersistencePort = loanTypePersistencePort;
    }

    @Override
    public Mono<LoanType> getLoanTypeById(Long id) {
        return loanTypePersistencePort.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                    String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND_OR_INACTIVE, id))));
    }

    @Override
    public Flux<LoanType> getAllActiveLoanTypes() {
        return loanTypePersistencePort.findAllActive();
    }

    @Override
    public Mono<LoanType> createLoanType(LoanType loanType) {
        return Mono.defer(() -> {
            try {
                validateLoanTypeSync(loanType);
                return loanTypePersistencePort.save(loanType);
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    @Override
    public Mono<LoanType> updateLoanType(Long id, LoanType loanType) {
        return loanTypePersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                    String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND, id))))
                .flatMap(existing -> Mono.defer(() -> {
                    try {
                        validateLoanTypeSync(loanType);
                        LoanType updatedLoanType = new LoanType(
                            id,
                            loanType.getName(),
                            loanType.getDescription(),
                            loanType.getMinAmount(),
                            loanType.getMaxAmount(),
                            loanType.getMinTermMonths(),
                            loanType.getMaxTermMonths(),
                            loanType.getInterestRate(),
                            loanType.getIsActive(),
                            loanType.getCreatedAt(),
                            loanType.getUpdatedAt()
                        );
                        return loanTypePersistencePort.update(updatedLoanType);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }));
    }

    @Override
    public Mono<Void> deleteLoanType(Long id) {
        return loanTypePersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                    String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND, id))))
                .then(Mono.defer(() -> loanTypePersistencePort.deleteById(id)));
    }

    private void validateLoanTypeSync(LoanType loanType) {
        if (loanType.getMinAmount().compareTo(loanType.getMaxAmount()) > 0) {
            throw new IllegalArgumentException(LoanTypeErrorMessages.MIN_AMOUNT_GREATER_THAN_MAX);
        }
        if (loanType.getMinTermMonths() > loanType.getMaxTermMonths()) {
            throw new IllegalArgumentException(LoanTypeErrorMessages.MIN_TERM_GREATER_THAN_MAX);
        }
        if (loanType.getInterestRate().signum() <= 0) {
            throw new IllegalArgumentException(LoanTypeErrorMessages.INTEREST_RATE_MUST_BE_POSITIVE);
        }
    }
}