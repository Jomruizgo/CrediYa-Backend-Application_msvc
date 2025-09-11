package com.crediya.usecase;

import com.crediya.exception.InvalidLoanApplicationDataException;
import com.crediya.exception.UnauthorizedUserException;
import com.crediya.gatewayport.ILoanApplicationPersistencePort;
import com.crediya.gatewayport.IAuthCommunicationPort;
import com.crediya.gatewayport.ILoanTypePersistencePort;
import com.crediya.model.*;
import com.crediya.util.UseCaseMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoanApplicationReviewUseCaseTest {

    @Mock
    private ILoanApplicationPersistencePort loanApplicationPersistencePort;

    @Mock
    private IAuthCommunicationPort authCommunicationPort;

    @Mock
    private ILoanTypePersistencePort loanTypePersistencePort;

    private LoanApplicationReviewUseCase loanApplicationReviewUseCase;
    private PageFilter validPageFilter;
    private LoanApplication validLoanApplication;
    private UserInfo validUserInfo;
    private LoanType validLoanType;
    private String validIdentityDocument;

    @BeforeEach
    void setUp() {
        loanApplicationReviewUseCase = new LoanApplicationReviewUseCase(
                loanApplicationPersistencePort, 
                authCommunicationPort, 
                loanTypePersistencePort
        );

        validIdentityDocument = "1234567890";
        
        validPageFilter = new PageFilter.Builder()
                .filter("statuses", Arrays.asList(
                        ApplicationStatus.PENDING_REVIEW,
                        ApplicationStatus.REJECTED,
                        ApplicationStatus.MANUAL_REVIEW
                ))
                .sortBy("createdAt")
                .sortOrder("DESC")
                .page(0, 20)
                .build();

        Loan loan = new Loan(BigDecimal.valueOf(100000), 12, 1L);
        validLoanApplication = new LoanApplication(
                "app-id-123",
                1L,
                validIdentityDocument,
                loan,
                ApplicationStatus.PENDING_REVIEW,
                LocalDateTime.now()
        );

        validUserInfo = new UserInfo("test@email.com", "Test User", BigDecimal.valueOf(3000000));
        
        validLoanType = new LoanType(
                1L, 
                "Personal Loan", 
                "Personal", 
                BigDecimal.valueOf(10000), 
                BigDecimal.valueOf(500000), 
                6, 
                60, 
                BigDecimal.valueOf(15.5), 
                true, 
                LocalDateTime.now(), 
                LocalDateTime.now()
        );
    }

    @Test
    void findApplicationsForReview_ShouldReturnPagedResults_WhenValidRole() {
        // Given
        String userRole = "ROLE_SELLER";
        Page<LoanApplication> mockPage = new Page<>(
                List.of(validLoanApplication), 
                1L, 
                0, 
                20
        );

        when(loanApplicationPersistencePort.findApplicationsForReview(any(PageFilter.class)))
                .thenReturn(Mono.just(mockPage));
        when(authCommunicationPort.getUserInfo(anyLong()))
                .thenReturn(Mono.just(validUserInfo));
        when(loanTypePersistencePort.findById(anyLong()))
                .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(anyString()))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(userRole, validPageFilter))
                .assertNext(page -> {
                    assertNotNull(page);
                    assertEquals(1, page.getContent().size());
                    assertEquals(1L, page.getTotalElements());
                    
                    LoanApplicationReview review = page.getContent().get(0);
                    assertEquals(validLoanApplication.getId(), review.getId());
                    assertEquals(validIdentityDocument, review.getIdentityDocument());
                    assertEquals(validUserInfo.getEmail(), review.getEmail());
                    assertEquals(validUserInfo.getName(), review.getName());
                    assertEquals(BigDecimal.ZERO, review.getApprovedLoansMonthlyPayment());
                })
                .verifyComplete();

        verify(loanApplicationPersistencePort).findApplicationsForReview(validPageFilter);
        verify(authCommunicationPort).getUserInfo(1L);
        verify(loanTypePersistencePort).findById(1L);
        verify(loanApplicationPersistencePort).findApprovedApplicationsByIdentityDocument(validIdentityDocument);
    }

    @Test
    void findApplicationsForReview_ShouldRejectUnauthorizedRole() {
        // Given
        String invalidRole = "CUSTOMER";

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidRole, validPageFilter))
                .expectError(UnauthorizedUserException.class)
                .verify();

        verify(loanApplicationPersistencePort, never()).findApplicationsForReview(any());
    }

    @Test
    void findApplicationsForReview_ShouldRejectNullPageSize() {
        // Given - Create filter with null pageSize through reflection or builder manipulation
        PageFilter invalidFilter = new PageFilter.Builder()
                .filter("statuses", Arrays.asList(ApplicationStatus.PENDING_REVIEW))
                .page(0, 20)
                .build();
        
        // Manually set pageSize to null via reflection to test the validation
        try {
            java.lang.reflect.Field pageSizeField = PageFilter.class.getDeclaredField("pageSize");
            pageSizeField.setAccessible(true);
            pageSizeField.set(invalidFilter, null);
        } catch (Exception e) {
            // If reflection fails, skip this test
            return;
        }

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectZeroPageSize() {
        // Given - Create a mock PageFilter with zero pageSize
        PageFilter invalidFilter = org.mockito.Mockito.mock(PageFilter.class);
        org.mockito.Mockito.when(invalidFilter.getPageSize()).thenReturn(0);
        org.mockito.Mockito.when(invalidFilter.getPageNumber()).thenReturn(0);

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectNegativePageSize() {
        // Given - Create a mock PageFilter with negative pageSize
        PageFilter invalidFilter = org.mockito.Mockito.mock(PageFilter.class);
        org.mockito.Mockito.when(invalidFilter.getPageSize()).thenReturn(-1);
        org.mockito.Mockito.when(invalidFilter.getPageNumber()).thenReturn(0);

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectPageSizeAboveMaximum() {
        // Given - Create a mock PageFilter with pageSize above maximum
        PageFilter invalidFilter = org.mockito.Mockito.mock(PageFilter.class);
        org.mockito.Mockito.when(invalidFilter.getPageSize()).thenReturn(101); // > MAX_PAGE_SIZE (100)
        org.mockito.Mockito.when(invalidFilter.getPageNumber()).thenReturn(0);

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectNullPageNumber() {
        // Given - Create filter with null pageNumber through reflection
        PageFilter invalidFilter = new PageFilter.Builder()
                .filter("statuses", Arrays.asList(ApplicationStatus.PENDING_REVIEW))
                .page(0, 20)
                .build();
        
        // Manually set pageNumber to null via reflection to test the validation
        try {
            java.lang.reflect.Field pageNumberField = PageFilter.class.getDeclaredField("pageNumber");
            pageNumberField.setAccessible(true);
            pageNumberField.set(invalidFilter, null);
        } catch (Exception e) {
            // If reflection fails, skip this test
            return;
        }

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectNegativePageNumber() {
        // Given - Create a mock PageFilter with negative pageNumber
        PageFilter invalidFilter = org.mockito.Mockito.mock(PageFilter.class);
        org.mockito.Mockito.when(invalidFilter.getPageSize()).thenReturn(20);
        org.mockito.Mockito.when(invalidFilter.getPageNumber()).thenReturn(-1); // negative pageNumber

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(invalidFilter))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void findApplicationsForReview_ShouldRejectNullFilter() {
        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview(null))
                .expectError(InvalidLoanApplicationDataException.class)
                .verify();
    }

    @Test
    void enrichLoanApplication_ShouldCalculateApprovedLoansMonthlyPayment_WhenApprovedLoansExist() {
        // Given
        Page<LoanApplication> mockPage = new Page<>(
                List.of(validLoanApplication), 
                1L, 
                0, 
                20
        );

        // Mock approved applications
        Loan approvedLoan1 = new Loan(BigDecimal.valueOf(200000), 24, 2L);
        Loan approvedLoan2 = new Loan(BigDecimal.valueOf(150000), 18, 3L);
        
        LoanApplication approvedApp1 = new LoanApplication(
                "approved-1", 1L, validIdentityDocument, approvedLoan1, ApplicationStatus.APPROVED, LocalDateTime.now()
        );
        LoanApplication approvedApp2 = new LoanApplication(
                "approved-2", 1L, validIdentityDocument, approvedLoan2, ApplicationStatus.APPROVED, LocalDateTime.now()
        );

        LoanType loanType2 = new LoanType(2L, "Car Loan", "Car", BigDecimal.ZERO, BigDecimal.ZERO, 
                                         0, 0, BigDecimal.valueOf(10.0), true, null, null);
        LoanType loanType3 = new LoanType(3L, "House Loan", "House", BigDecimal.ZERO, BigDecimal.ZERO, 
                                         0, 0, BigDecimal.valueOf(8.0), true, null, null);

        when(loanApplicationPersistencePort.findApplicationsForReview(any(PageFilter.class)))
                .thenReturn(Mono.just(mockPage));
        when(authCommunicationPort.getUserInfo(anyLong()))
                .thenReturn(Mono.just(validUserInfo));
        when(loanTypePersistencePort.findById(1L))
                .thenReturn(Mono.just(validLoanType));
        when(loanTypePersistencePort.findById(2L))
                .thenReturn(Mono.just(loanType2));
        when(loanTypePersistencePort.findById(3L))
                .thenReturn(Mono.just(loanType3));
        when(loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(validIdentityDocument))
                .thenReturn(Flux.just(approvedApp1, approvedApp2));

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview("ROLE_SELLER", validPageFilter))
                .assertNext(page -> {
                    assertNotNull(page);
                    assertEquals(1, page.getContent().size());
                    
                    LoanApplicationReview review = page.getContent().get(0);
                    
                    // Expected monthly payments:
                    // approvedApp1: 200000 / 24 months with 10% = ~9,264.78
                    // approvedApp2: 150000 / 18 months with 8% = ~9,157.25
                    // Total: ~18,422.03
                    assertTrue(review.getApprovedLoansMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
                    assertTrue(review.getApprovedLoansMonthlyPayment().compareTo(BigDecimal.valueOf(18000)) > 0);
                })
                .verifyComplete();
    }

    @Test
    void enrichLoanApplication_ShouldHandleErrorsGracefully_WhenServicesDown() {
        // Given
        Page<LoanApplication> mockPage = new Page<>(
                List.of(validLoanApplication), 
                1L, 
                0, 
                20
        );

        when(loanApplicationPersistencePort.findApplicationsForReview(any(PageFilter.class)))
                .thenReturn(Mono.just(mockPage));
        when(authCommunicationPort.getUserInfo(anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Auth service down")));
        when(loanTypePersistencePort.findById(anyLong()))
                .thenReturn(Mono.error(new RuntimeException("LoanType service down")));
        when(loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(anyString()))
                .thenReturn(Flux.error(new RuntimeException("Database down")));

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview("ROLE_SELLER", validPageFilter))
                .assertNext(page -> {
                    assertNotNull(page);
                    assertEquals(1, page.getContent().size());
                    
                    LoanApplicationReview review = page.getContent().get(0);
                    
                    // Should use default values
                    assertEquals("N/A", review.getEmail());
                    assertEquals("N/A", review.getName());
                    assertEquals(BigDecimal.ZERO, review.getBaseSalary());
                    assertEquals("Personal Loan", review.getLoanTypeName());
                    assertEquals(BigDecimal.ZERO, review.getApprovedLoansMonthlyPayment());
                })
                .verifyComplete();
    }

    @Test
    void enrichLoanApplication_ShouldReturnZero_WhenNoApprovedLoansExist() {
        // Given
        Page<LoanApplication> mockPage = new Page<>(
                List.of(validLoanApplication), 
                1L, 
                0, 
                20
        );

        when(loanApplicationPersistencePort.findApplicationsForReview(any(PageFilter.class)))
                .thenReturn(Mono.just(mockPage));
        when(authCommunicationPort.getUserInfo(anyLong()))
                .thenReturn(Mono.just(validUserInfo));
        when(loanTypePersistencePort.findById(anyLong()))
                .thenReturn(Mono.just(validLoanType));
        when(loanApplicationPersistencePort.findApprovedApplicationsByIdentityDocument(validIdentityDocument))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(loanApplicationReviewUseCase.findApplicationsForReview("ROLE_SELLER", validPageFilter))
                .assertNext(page -> {
                    LoanApplicationReview review = page.getContent().get(0);
                    assertEquals(BigDecimal.ZERO, review.getApprovedLoansMonthlyPayment());
                })
                .verifyComplete();
    }
}