package com.crediya.usecase;

import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.LoanType;
import com.crediya.util.LoanTypeErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanTypeUseCaseTest {

    @Mock
    private ILoanTypePersistencePort loanTypePersistencePort;

    private LoanTypeUseCase loanTypeUseCase;
    private LoanType validLoanType;

    @BeforeEach
    void setUp() {
        loanTypeUseCase = new LoanTypeUseCase(loanTypePersistencePort);
        
        validLoanType = new LoanType(
            1L,
            "Personal Loan",
            "Personal loan for individual needs",
            new BigDecimal("500000"),
            new BigDecimal("5000000"),
            6,
            60,
            new BigDecimal("15.5"),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void getLoanTypeById_ShouldReturnLoanType_WhenExists() {
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));

        StepVerifier.create(loanTypeUseCase.getLoanTypeById(1L))
            .expectNext(validLoanType)
            .verifyComplete();

        verify(loanTypePersistencePort).findByIdAndActive(1L);
    }

    @Test
    void getLoanTypeById_ShouldThrowException_WhenNotFound() {
        when(loanTypePersistencePort.findByIdAndActive(999L))
            .thenReturn(Mono.empty());

        StepVerifier.create(loanTypeUseCase.getLoanTypeById(999L))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND_OR_INACTIVE, 999L))
            )
            .verify();

        verify(loanTypePersistencePort).findByIdAndActive(999L);
    }

    @Test
    void getAllActiveLoanTypes_ShouldReturnAllActiveLoanTypes() {
        LoanType loanType2 = new LoanType(
            2L, "Vehicle Loan", "Loan for vehicles", 
            new BigDecimal("1000000"), new BigDecimal("10000000"),
            12, 72, new BigDecimal("12.0"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(loanTypePersistencePort.findAllActive())
            .thenReturn(Flux.just(validLoanType, loanType2));

        StepVerifier.create(loanTypeUseCase.getAllActiveLoanTypes())
            .expectNext(validLoanType)
            .expectNext(loanType2)
            .verifyComplete();

        verify(loanTypePersistencePort).findAllActive();
    }

    @Test
    void getAllActiveLoanTypes_ShouldReturnEmpty_WhenNoActiveLoanTypes() {
        when(loanTypePersistencePort.findAllActive())
            .thenReturn(Flux.empty());

        StepVerifier.create(loanTypeUseCase.getAllActiveLoanTypes())
            .verifyComplete();

        verify(loanTypePersistencePort).findAllActive();
    }

    @Test
    void createLoanType_ShouldCreateSuccessfully_WithValidData() {
        when(loanTypePersistencePort.save(any(LoanType.class)))
            .thenReturn(Mono.just(validLoanType));

        StepVerifier.create(loanTypeUseCase.createLoanType(validLoanType))
            .expectNext(validLoanType)
            .verifyComplete();

        verify(loanTypePersistencePort).save(validLoanType);
    }

    @Test
    void createLoanType_ShouldThrowException_WhenMinAmountGreaterThanMax() {
        LoanType invalidLoanType = new LoanType(
            null, "Invalid Loan", "Invalid loan with wrong amounts",
            new BigDecimal("6000000"), // min > max
            new BigDecimal("5000000"),
            6, 60, new BigDecimal("15.5"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        StepVerifier.create(loanTypeUseCase.createLoanType(invalidLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(LoanTypeErrorMessages.MIN_AMOUNT_GREATER_THAN_MAX)
            )
            .verify();

        verify(loanTypePersistencePort, never()).save(any(LoanType.class));
    }

    @Test
    void createLoanType_ShouldThrowException_WhenMinTermGreaterThanMax() {
        LoanType invalidLoanType = new LoanType(
            null, "Invalid Loan", "Invalid loan with wrong terms",
            new BigDecimal("500000"), new BigDecimal("5000000"),
            72, // min > max
            60,
            new BigDecimal("15.5"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        StepVerifier.create(loanTypeUseCase.createLoanType(invalidLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(LoanTypeErrorMessages.MIN_TERM_GREATER_THAN_MAX)
            )
            .verify();

        verify(loanTypePersistencePort, never()).save(any(LoanType.class));
    }

    @Test
    void createLoanType_ShouldThrowException_WhenInterestRateIsZero() {
        LoanType invalidLoanType = new LoanType(
            null, "Invalid Loan", "Invalid loan with zero interest",
            new BigDecimal("500000"), new BigDecimal("5000000"),
            6, 60, BigDecimal.ZERO, true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        StepVerifier.create(loanTypeUseCase.createLoanType(invalidLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(LoanTypeErrorMessages.INTEREST_RATE_MUST_BE_POSITIVE)
            )
            .verify();

        verify(loanTypePersistencePort, never()).save(any(LoanType.class));
    }

    @Test
    void createLoanType_ShouldThrowException_WhenInterestRateIsNegative() {
        LoanType invalidLoanType = new LoanType(
            null, "Invalid Loan", "Invalid loan with negative interest",
            new BigDecimal("500000"), new BigDecimal("5000000"),
            6, 60, new BigDecimal("-5.0"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        StepVerifier.create(loanTypeUseCase.createLoanType(invalidLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(LoanTypeErrorMessages.INTEREST_RATE_MUST_BE_POSITIVE)
            )
            .verify();

        verify(loanTypePersistencePort, never()).save(any(LoanType.class));
    }

    @Test
    void updateLoanType_ShouldUpdateSuccessfully_WhenLoanTypeExists() {
        LoanType updatedLoanType = new LoanType(
            1L, "Updated Personal Loan", "Updated description",
            new BigDecimal("600000"), new BigDecimal("6000000"),
            12, 48, new BigDecimal("14.0"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(loanTypePersistencePort.findById(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanTypePersistencePort.update(any(LoanType.class)))
            .thenReturn(Mono.just(updatedLoanType));

        StepVerifier.create(loanTypeUseCase.updateLoanType(1L, updatedLoanType))
            .expectNext(updatedLoanType)
            .verifyComplete();

        verify(loanTypePersistencePort).findById(1L);
        verify(loanTypePersistencePort).update(any(LoanType.class));
    }

    @Test
    void updateLoanType_ShouldThrowException_WhenLoanTypeNotFound() {
        when(loanTypePersistencePort.findById(999L))
            .thenReturn(Mono.empty());

        StepVerifier.create(loanTypeUseCase.updateLoanType(999L, validLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND, 999L))
            )
            .verify();

        verify(loanTypePersistencePort).findById(999L);
        verify(loanTypePersistencePort, never()).update(any(LoanType.class));
    }

    @Test
    void updateLoanType_ShouldThrowException_WhenValidationFails() {
        LoanType invalidUpdate = new LoanType(
            1L, "Invalid Update", "Invalid update with wrong amounts",
            new BigDecimal("6000000"), // min > max
            new BigDecimal("5000000"),
            6, 60, new BigDecimal("15.5"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(loanTypePersistencePort.findById(1L))
            .thenReturn(Mono.just(validLoanType));

        StepVerifier.create(loanTypeUseCase.updateLoanType(1L, invalidUpdate))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().equals(LoanTypeErrorMessages.MIN_AMOUNT_GREATER_THAN_MAX)
            )
            .verify();

        verify(loanTypePersistencePort).findById(1L);
        verify(loanTypePersistencePort, never()).update(any(LoanType.class));
    }

    @Test
    void deleteLoanType_ShouldDeleteSuccessfully_WhenLoanTypeExists() {
        when(loanTypePersistencePort.findById(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanTypePersistencePort.deleteById(1L))
            .thenReturn(Mono.empty());

        StepVerifier.create(loanTypeUseCase.deleteLoanType(1L))
            .verifyComplete();

        verify(loanTypePersistencePort).findById(1L);
        verify(loanTypePersistencePort).deleteById(1L);
    }

    @Test
    void deleteLoanType_ShouldThrowException_WhenLoanTypeNotFound() {
        when(loanTypePersistencePort.findById(999L))
            .thenReturn(Mono.empty());

        StepVerifier.create(loanTypeUseCase.deleteLoanType(999L))
            .expectErrorMatches(throwable -> {
                System.out.println("DEBUG LoanType - Error type: " + throwable.getClass().getSimpleName());
                System.out.println("DEBUG LoanType - Error message: " + throwable.getMessage());
                if (throwable instanceof NullPointerException) {
                    throwable.printStackTrace();
                }
                return throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(String.format(LoanTypeErrorMessages.LOAN_TYPE_NOT_FOUND, 999L));
            })
            .verify();

        verify(loanTypePersistencePort).findById(999L);
        verify(loanTypePersistencePort, never()).deleteById(anyLong());
    }

    @Test
    void createLoanType_ShouldHandlePersistenceError() {
        when(loanTypePersistencePort.save(any(LoanType.class)))
            .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        StepVerifier.create(loanTypeUseCase.createLoanType(validLoanType))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Database connection error")
            )
            .verify();

        verify(loanTypePersistencePort).save(validLoanType);
    }
}