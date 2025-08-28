package com.crediya.usecase;

import com.crediya.exception.LoanApplicationException;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.Loan;
import com.crediya.model.LoanApplication;
import com.crediya.model.LoanType;
import com.crediya.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationUseCaseTest {

    @Mock
    private ILoanApplicationPersistencePort loanApplicationPersistencePort;

    private LoanApplicationUseCase loanApplicationUseCase;
    private Loan validLoan;
    private String validIdentityDocument;

    @BeforeEach
    void setUp() {
        loanApplicationUseCase = new LoanApplicationUseCase(loanApplicationPersistencePort);
        
        validLoan = new Loan(
            new BigDecimal("1000000.00"),
            12,
            LoanType.PERSONAL
        );
        
        validIdentityDocument = "12345678";
    }

    @Test
    void shouldExecuteLoanApplicationSuccessfully() {
        when(loanApplicationPersistencePort.save(any(LoanApplication.class)))
            .thenAnswer(invocation -> {
                LoanApplication application = invocation.getArgument(0);
                return Mono.just(application);
            });

        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, validLoan))
            .assertNext(application -> {
                assertEquals(validIdentityDocument, application.getIdentityDocument());
                assertEquals(validLoan, application.getLoan());
                assertEquals(ApplicationStatus.PENDING_REVIEW, application.getStatus());
                assertNotNull(application.getId());
                assertNotNull(application.getCreatedAt());
            })
            .verifyComplete();

        verify(loanApplicationPersistencePort).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailWhenIdentityDocumentIsNull() {
        StepVerifier.create(loanApplicationUseCase.execute(null, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.IDENTITY_DOCUMENT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentIsEmpty() {
        StepVerifier.create(loanApplicationUseCase.execute("", validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.IDENTITY_DOCUMENT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentIsBlank() {
        StepVerifier.create(loanApplicationUseCase.execute("   ", validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.IDENTITY_DOCUMENT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanIsNull() {
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, null))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.LOAN_TYPE_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsNull() {
        Loan invalidLoan = new Loan(null, 12, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.AMOUNT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsZero() {
        Loan invalidLoan = new Loan(BigDecimal.ZERO, 12, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.AMOUNT_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsNegative() {
        Loan invalidLoan = new Loan(new BigDecimal("-1000"), 12, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.AMOUNT_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsNull() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), null, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.TERM_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsZero() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), 0, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.TERM_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsNegative() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), -12, LoanType.PERSONAL);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.TERM_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanTypeIsNull() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), 12, null);
        
        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationException &&
                throwable.getMessage().equals(Constant.LOAN_TYPE_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldHandlePersistenceError() {
        when(loanApplicationPersistencePort.save(any(LoanApplication.class)))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Database error")
            )
            .verify();
    }

    @Test
    void shouldWorkWithDifferentLoanTypes() {
        for (LoanType loanType : LoanType.values()) {
            Loan loan = new Loan(new BigDecimal("2000000"), 24, loanType);
            
            LoanApplication expectedApplication = new LoanApplication(
                "id", validIdentityDocument, loan, ApplicationStatus.PENDING_REVIEW, null
            );
            
            when(loanApplicationPersistencePort.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(expectedApplication));

            StepVerifier.create(loanApplicationUseCase.execute(validIdentityDocument, loan))
                .assertNext(application -> assertEquals(loanType, application.getLoan().getType()))
                .verifyComplete();
        }
    }

}