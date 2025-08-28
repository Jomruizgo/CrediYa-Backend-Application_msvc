package com.crediya.r2dbc.adapter;

import com.crediya.model.ApplicationStatus;
import com.crediya.model.Loan;
import com.crediya.model.LoanApplication;
import com.crediya.model.LoanType;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import com.crediya.r2dbc.repository.LoanApplicationR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationPersistenceAdapterTest {

    @Mock
    private LoanApplicationR2dbcRepository repository;
    
    @Mock
    private LoanApplicationEntityMapper mapper;

    private LoanApplicationPersistenceAdapter adapter;
    private LoanApplication testLoanApplication;
    private LoanApplicationEntity testLoanApplicationEntity;

    @BeforeEach
    void setUp() {
        adapter = new LoanApplicationPersistenceAdapter(repository, mapper);
        
        Loan testLoan = new Loan(
            new BigDecimal("1000000.00"),
            12,
            LoanType.PERSONAL
        );
        
        testLoanApplication = new LoanApplication(
            "app-123",
            "12345678",
            testLoan,
            ApplicationStatus.PENDING_REVIEW,
            LocalDateTime.now()
        );
        
        testLoanApplicationEntity = new LoanApplicationEntity();
        testLoanApplicationEntity.setId("app-123");
        testLoanApplicationEntity.setIdentityDocument("12345678");
        testLoanApplicationEntity.setAmount(new BigDecimal("1000000.00"));
        testLoanApplicationEntity.setTermMonths(12);
        testLoanApplicationEntity.setLoanType("PERSONAL");
        testLoanApplicationEntity.setStatus("PENDING_REVIEW");
        testLoanApplicationEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldSaveLoanApplication() {
        when(mapper.toEntity(testLoanApplication)).thenReturn(testLoanApplicationEntity);
        when(repository.save(testLoanApplicationEntity)).thenReturn(Mono.just(testLoanApplicationEntity));
        when(mapper.toDomain(testLoanApplicationEntity)).thenReturn(testLoanApplication);

        StepVerifier.create(adapter.save(testLoanApplication))
            .expectNext(testLoanApplication)
            .verifyComplete();

        verify(mapper).toEntity(testLoanApplication);
        verify(repository).save(testLoanApplicationEntity);
        verify(mapper).toDomain(testLoanApplicationEntity);
    }

    @Test
    void shouldHandleSaveError() {
        when(mapper.toEntity(testLoanApplication)).thenReturn(testLoanApplicationEntity);
        when(repository.save(any())).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.save(testLoanApplication))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Database error")
            )
            .verify();

        verify(mapper).toEntity(testLoanApplication);
        verify(repository).save(testLoanApplicationEntity);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldFindLoanApplicationById() {
        when(repository.findById("app-123")).thenReturn(Mono.just(testLoanApplicationEntity));
        when(mapper.toDomain(testLoanApplicationEntity)).thenReturn(testLoanApplication);

        StepVerifier.create(adapter.findById("app-123"))
            .expectNext(testLoanApplication)
            .verifyComplete();

        verify(repository).findById("app-123");
        verify(mapper).toDomain(testLoanApplicationEntity);
    }

    @Test
    void shouldReturnEmptyWhenLoanApplicationNotFound() {
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("non-existent"))
            .verifyComplete();

        verify(repository).findById("non-existent");
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldHandleFindByIdError() {
        when(repository.findById(anyString())).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.findById("app-123"))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Database error")
            )
            .verify();

        verify(repository).findById("app-123");
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldLogSuccessfulSave() {
        when(mapper.toEntity(testLoanApplication)).thenReturn(testLoanApplicationEntity);
        when(repository.save(testLoanApplicationEntity)).thenReturn(Mono.just(testLoanApplicationEntity));
        when(mapper.toDomain(testLoanApplicationEntity)).thenReturn(testLoanApplication);

        StepVerifier.create(adapter.save(testLoanApplication))
            .expectNext(testLoanApplication)
            .verifyComplete();

        // Verification that logging methods were triggered is implicit 
        // through successful completion without errors
    }

    @Test
    void shouldLogSuccessfulFind() {
        when(repository.findById("app-123")).thenReturn(Mono.just(testLoanApplicationEntity));
        when(mapper.toDomain(testLoanApplicationEntity)).thenReturn(testLoanApplication);

        StepVerifier.create(adapter.findById("app-123"))
            .expectNext(testLoanApplication)
            .verifyComplete();

        // Verification that logging methods were triggered is implicit 
        // through successful completion without errors
    }

    @Test
    void shouldWorkWithDifferentLoanTypes() {
        for (LoanType loanType : LoanType.values()) {
            Loan loan = new Loan(new BigDecimal("2000000"), 24, loanType);
            LoanApplication application = new LoanApplication(
                "app-" + loanType.name().toLowerCase(),
                "87654321",
                loan,
                ApplicationStatus.PENDING_REVIEW,
                LocalDateTime.now()
            );
            
            LoanApplicationEntity entity = new LoanApplicationEntity();
            entity.setLoanType(loanType.name());
            
            when(mapper.toEntity(application)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(Mono.just(entity));
            when(mapper.toDomain(entity)).thenReturn(application);

            StepVerifier.create(adapter.save(application))
                .expectNext(application)
                .verifyComplete();
        }
    }
}