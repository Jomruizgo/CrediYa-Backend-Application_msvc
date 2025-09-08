package com.crediya.usecase;

import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.exception.LoanApplicationAlreadyExistsException;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.Loan;
import com.crediya.model.LoanApplication;
import com.crediya.model.LoanType;
import com.crediya.util.UseCaseMessages;
import com.crediya.exception.UnauthorizedUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoanApplicationUseCaseTest {

    @Mock
    private ILoanApplicationPersistencePort loanApplicationPersistencePort;

    @Mock
    private IAuthCommunicationPort authCommunicationPort;

    @Mock
    private ILoanTypePersistencePort loanTypePersistencePort;

    private LoanApplicationUseCase loanApplicationUseCase;
    private Loan validLoan;
    private String validIdentityDocument;
    private Long validUserId;
    private LoanType validLoanType;

    @BeforeEach
    void setUp() {
        loanApplicationUseCase = new LoanApplicationUseCase(
            loanApplicationPersistencePort,
            authCommunicationPort,
            loanTypePersistencePort
        );
        
        validUserId = 123L;
        validIdentityDocument = "12345678";
        
        validLoanType = new LoanType(
            1L, "Personal Loan", "Personal loan for individual needs",
            new BigDecimal("500000"), new BigDecimal("5000000"),
            6, 60, new BigDecimal("15.5"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        validLoan = new Loan(
            new BigDecimal("1000000.00"),
            12,
            1L
        );
        
        // Mocks básicos requeridos para todos los tests debido al flujo reactivo
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(anyString(), any(ApplicationStatus.class)))
            .thenReturn(Mono.empty());
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(isNull(), any(ApplicationStatus.class)))
            .thenReturn(Mono.empty());
        when(loanTypePersistencePort.findByIdAndActive(anyLong()))
            .thenReturn(Mono.just(validLoanType));
        when(loanTypePersistencePort.findByIdAndActive(null))
            .thenReturn(Mono.empty());
        when(authCommunicationPort.validateAndUpdateUserDocument(anyLong(), anyString()))
            .thenReturn(Mono.empty());
        when(loanApplicationPersistencePort.save(any()))
            .thenReturn(Mono.just(mock(LoanApplication.class)));
    }

    @Test
    void shouldExecuteLoanApplicationSuccessfully() {
        // Setup mocks
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(authCommunicationPort.validateAndUpdateUserDocument(validUserId, validIdentityDocument))
            .thenReturn(Mono.empty());
        when(loanApplicationPersistencePort.save(any(LoanApplication.class)))
            .thenAnswer(invocation -> {
                LoanApplication application = invocation.getArgument(0);
                return Mono.just(application);
            });

        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, validLoan))
            .assertNext(application -> {
                assertEquals(validIdentityDocument, application.getIdentityDocument());
                assertEquals(validLoan, application.getLoan());
                assertEquals(ApplicationStatus.PENDING_REVIEW, application.getStatus());
                assertNotNull(application.getId());
                assertNotNull(application.getCreatedAt());
            })
            .verifyComplete();

        verify(loanTypePersistencePort).findByIdAndActive(1L);
        verify(loanApplicationPersistencePort).findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW);
        verify(authCommunicationPort).validateAndUpdateUserDocument(validUserId, validIdentityDocument);
        verify(loanApplicationPersistencePort).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailWhenUserRoleIsNotClient() {
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, "ROLE_ADMIN", validIdentityDocument, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof UnauthorizedUserException &&
                throwable.getMessage().contains("ROLE_ADMIN")
            )
            .verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentIsNull() {
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, null, validLoan))
            .expectError(InvalidLoanApplicationDataException.class)
            .verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentIsEmpty() {
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, "", validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.IDENTITY_DOCUMENT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentIsBlank() {
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, "   ", validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.IDENTITY_DOCUMENT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanIsNull() {
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, null))
            .expectError(InvalidLoanApplicationDataException.class)
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsNull() {
        Loan invalidLoan = new Loan(null, 12, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.AMOUNT_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsZero() {
        Loan invalidLoan = new Loan(BigDecimal.ZERO, 12, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.AMOUNT_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountIsNegative() {
        Loan invalidLoan = new Loan(new BigDecimal("-1000"), 12, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.AMOUNT_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsNull() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), null, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.TERM_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsZero() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), 0, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.TERM_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsNegative() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), -12, 1L);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.TERM_MUST_BE_POSITIVE)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanTypeIsNull() {
        Loan invalidLoan = new Loan(new BigDecimal("1000000"), 12, null);
        
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals(UseCaseMessages.LOAN_TYPE_REQUIRED)
            )
            .verify();
    }

    @Test
    void shouldFailWhenLoanAmountExceedsMaximum() {
        Loan exceedsMaxLoan = new Loan(new BigDecimal("6000000"), 12, 1L); // Exceeds validLoanType max of 5M
        
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, exceedsMaxLoan))
            .expectErrorMatches(throwable -> {
                System.out.println("DEBUG - Error type: " + throwable.getClass().getSimpleName());
                System.out.println("DEBUG - Error message: " + throwable.getMessage());
                if (throwable instanceof NullPointerException) {
                    throwable.printStackTrace();
                }
                return throwable instanceof InvalidLoanApplicationDataException &&
                    throwable.getMessage().contains("Amount 6000000 exceeds maximum 5000000 for loan type Personal Loan");
            })
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
    }

    @Test
    void shouldFailWhenLoanAmountBelowMinimum() {
        Loan belowMinLoan = new Loan(new BigDecimal("300000"), 12, 1L); // Below validLoanType min of 500K
        
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, belowMinLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().contains("Amount 300000 is below minimum 500000 for loan type Personal Loan")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
    }

    @Test
    void shouldFailWhenTermExceedsMaximum() {
        Loan exceedsMaxTermLoan = new Loan(new BigDecimal("1000000"), 72, 1L); // Exceeds validLoanType max of 60
        
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, exceedsMaxTermLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().contains("Term 72 months exceeds maximum 60 for loan type Personal Loan")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
    }

    @Test
    void shouldFailWhenTermBelowMinimum() {
        Loan belowMinTermLoan = new Loan(new BigDecimal("1000000"), 3, 1L); // Below validLoanType min of 6
        
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, belowMinTermLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().contains("Term 3 months is below minimum 6 for loan type Personal Loan")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        Loan invalidLoanTypeLoan = new Loan(new BigDecimal("1000000"), 12, 999L);
        
        when(loanTypePersistencePort.findByIdAndActive(999L))
            .thenReturn(Mono.empty());
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, invalidLoanTypeLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals("Invalid loan type ID: 999")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(999L);
    }

    @Test
    void shouldFailWhenPendingApplicationExists() {
        LoanApplication existingApplication = new LoanApplication(
            "existing-id", validIdentityDocument, validLoan, ApplicationStatus.PENDING_REVIEW, LocalDateTime.now()
        );
        
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.just(existingApplication));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof LoanApplicationAlreadyExistsException &&
                throwable.getMessage().equals(UseCaseMessages.LOAN_APPLICATION_ALREADY_EXISTS)
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
        verify(loanApplicationPersistencePort).findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW);
    }

    @Test
    void shouldFailWhenAuthCommunicationFails() {
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(authCommunicationPort.validateAndUpdateUserDocument(validUserId, validIdentityDocument))
            .thenReturn(Mono.error(new RuntimeException("Auth service unavailable")));
            
        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Auth service unavailable")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
        verify(loanApplicationPersistencePort).findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW);
        verify(authCommunicationPort).validateAndUpdateUserDocument(validUserId, validIdentityDocument);
        verify(loanApplicationPersistencePort, never()).save(any(LoanApplication.class));
    }

    @Test
    void findById_ShouldReturnLoanApplication_WhenExists() {
        String applicationId = "test-id-123";
        LoanApplication expectedApplication = new LoanApplication(
            applicationId, validIdentityDocument, validLoan, ApplicationStatus.PENDING_REVIEW, LocalDateTime.now()
        );
        
        when(loanApplicationPersistencePort.findById(applicationId))
            .thenReturn(Mono.just(expectedApplication));
            
        StepVerifier.create(loanApplicationUseCase.findById(applicationId))
            .expectNext(expectedApplication)
            .verifyComplete();
            
        verify(loanApplicationPersistencePort).findById(applicationId);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        String applicationId = "non-existent-id";
        
        when(loanApplicationPersistencePort.findById(applicationId))
            .thenReturn(Mono.empty());
            
        StepVerifier.create(loanApplicationUseCase.findById(applicationId))
            .expectErrorMatches(throwable -> 
                throwable instanceof InvalidLoanApplicationDataException &&
                throwable.getMessage().equals("Loan application not found with id: " + applicationId)
            )
            .verify();
            
        verify(loanApplicationPersistencePort).findById(applicationId);
    }

    @Test
    void shouldHandlePersistenceError() {
        when(loanTypePersistencePort.findByIdAndActive(1L))
            .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW))
            .thenReturn(Mono.empty());
        when(authCommunicationPort.validateAndUpdateUserDocument(validUserId, validIdentityDocument))
            .thenReturn(Mono.empty());
        when(loanApplicationPersistencePort.save(any(LoanApplication.class)))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(loanApplicationUseCase.execute(validUserId, UseCaseMessages.CLIENT_ROLE, validIdentityDocument, validLoan))
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Database error")
            )
            .verify();
            
        verify(loanTypePersistencePort).findByIdAndActive(1L);
        verify(loanApplicationPersistencePort).findByIdentityDocumentAndStatus(validIdentityDocument, ApplicationStatus.PENDING_REVIEW);
        verify(authCommunicationPort).validateAndUpdateUserDocument(validUserId, validIdentityDocument);
        verify(loanApplicationPersistencePort).save(any(LoanApplication.class));
    }

}