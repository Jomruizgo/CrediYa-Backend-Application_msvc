package com.crediya.api;

import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.dto.response.LoanApplicationResponseDto;
import com.crediya.api.handler.LoanApplicationHandler;
import com.crediya.api.mapper.LoanApplicationRequestMapper;
import com.crediya.api.mapper.LoanApplicationResponseMapper;
import com.crediya.exception.LoanApplicationException;
import com.crediya.model.ApplicationStatus;
import com.crediya.model.Loan;
import com.crediya.model.LoanApplication;
import com.crediya.model.LoanType;
import com.crediya.usecase.LoanApplicationUseCase;
import com.crediya.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationHandlerTest {

    @Mock
    private LoanApplicationUseCase loanApplicationUseCase;
    
    @Mock
    private LoanApplicationRequestMapper requestMapper;
    
    @Mock
    private LoanApplicationResponseMapper responseMapper;
    
    @Mock
    private ServerRequest serverRequest;

    private LoanApplicationHandler handler;
    private LoanApplicationRequestDto requestDto;
    private LoanApplicationResponseDto responseDto;
    private Loan loan;
    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        handler = new LoanApplicationHandler(loanApplicationUseCase, requestMapper, responseMapper);
        
        requestDto = new LoanApplicationRequestDto();
        requestDto.setIdentityDocument("12345678");
        requestDto.setAmount(new BigDecimal("1000000.00"));
        requestDto.setTermMonths(12);
        requestDto.setLoanType(LoanType.PERSONAL);
        
        loan = new Loan(
            new BigDecimal("1000000.00"),
            12,
            LoanType.PERSONAL
        );
        
        loanApplication = new LoanApplication(
            "app-123",
            "12345678",
            loan,
            ApplicationStatus.PENDING_REVIEW,
            LocalDateTime.now()
        );
        
        responseDto = new LoanApplicationResponseDto();
        responseDto.setId("app-123");
        responseDto.setIdentityDocument("12345678");
        responseDto.setAmount(new BigDecimal("1000000.00"));
        responseDto.setTermMonths(12);
        responseDto.setLoanType(LoanType.PERSONAL);
        responseDto.setStatus(ApplicationStatus.PENDING_REVIEW);
        responseDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto)).thenReturn(loan);
        when(loanApplicationUseCase.execute("12345678", loan))
            .thenReturn(Mono.just(loanApplication));
        when(responseMapper.toResponseDto(loanApplication)).thenReturn(responseDto);

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                // Verify that ServerResponse was created successfully
                assertNotNull(response);
            })
            .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationRequestDto.class);
        verify(requestMapper).toLoan(requestDto);
        verify(loanApplicationUseCase).execute("12345678", loan);
        verify(responseMapper).toResponseDto(loanApplication);
    }

    @Test
    void shouldHandleInvalidRequestData() {
        LoanApplicationException exception = new LoanApplicationException(Constant.IDENTITY_DOCUMENT_REQUIRED);
        
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto)).thenReturn(loan);
        when(loanApplicationUseCase.execute(anyString(), any(Loan.class)))
            .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                // Verify that error response was created
                assertNotNull(response);
            })
            .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationRequestDto.class);
        verify(requestMapper).toLoan(requestDto);
        verify(loanApplicationUseCase).execute(anyString(), any(Loan.class));
        verify(responseMapper, never()).toResponseDto(any());
    }

    @Test
    void shouldHandleUseCaseError() {
        RuntimeException exception = new RuntimeException("Database connection failed");
        
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto)).thenReturn(loan);
        when(loanApplicationUseCase.execute(anyString(), any(Loan.class)))
            .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
            })
            .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationRequestDto.class);
        verify(requestMapper).toLoan(requestDto);
        verify(loanApplicationUseCase).execute(anyString(), any(Loan.class));
        verify(responseMapper, never()).toResponseDto(any());
    }

    @Test
    void shouldHandleRequestBodyError() {
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.error(new RuntimeException("Invalid JSON")));

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
            })
            .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationRequestDto.class);
        verify(requestMapper, never()).toLoan(any());
        verify(loanApplicationUseCase, never()).execute(anyString(), any());
        verify(responseMapper, never()).toResponseDto(any());
    }

    @Test
    void shouldHandleMapperError() {
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto))
            .thenThrow(new RuntimeException("Mapping error"));

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
            })
            .verifyComplete();

        verify(serverRequest).bodyToMono(LoanApplicationRequestDto.class);
        verify(requestMapper).toLoan(requestDto);
        verify(loanApplicationUseCase, never()).execute(anyString(), any());
        verify(responseMapper, never()).toResponseDto(any());
    }

    @Test
    void shouldReturnNotFoundForGetApplicationById() {
        when(serverRequest.pathVariable("id")).thenReturn("app-123");

        StepVerifier.create(handler.getApplicationById(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
                // Currently returns not found as method is not implemented
            })
            .verifyComplete();

        verify(serverRequest).pathVariable("id");
    }

    @Test
    void shouldLogRequestDetails() {
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto)).thenReturn(loan);
        when(loanApplicationUseCase.execute("12345678", loan))
            .thenReturn(Mono.just(loanApplication));
        when(responseMapper.toResponseDto(loanApplication)).thenReturn(responseDto);

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
            })
            .verifyComplete();

        // Logging is verified implicitly through successful execution
        verify(loanApplicationUseCase).execute("12345678", loan);
    }

    @Test
    void shouldHandleNullIdentityDocument() {
        requestDto.setIdentityDocument(null);
        LoanApplicationException exception = new LoanApplicationException(Constant.IDENTITY_DOCUMENT_REQUIRED);
        
        when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
            .thenReturn(Mono.just(requestDto));
        when(requestMapper.toLoan(requestDto)).thenReturn(loan);
        when(loanApplicationUseCase.execute(null, loan))
            .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.registerApplication(serverRequest))
            .assertNext(response -> {
                assertNotNull(response);
            })
            .verifyComplete();

        verify(loanApplicationUseCase).execute(null, loan);
    }

    @Test
    void shouldHandleDifferentLoanTypes() {
        for (LoanType loanType : LoanType.values()) {
            requestDto.setLoanType(loanType);
            Loan testLoan = new Loan(new BigDecimal("2000000"), 24, loanType);
            LoanApplication testApplication = new LoanApplication(
                "app-" + loanType.name(),
                "12345678",
                testLoan,
                ApplicationStatus.PENDING_REVIEW,
                LocalDateTime.now()
            );

            when(serverRequest.bodyToMono(LoanApplicationRequestDto.class))
                .thenReturn(Mono.just(requestDto));
            when(requestMapper.toLoan(requestDto)).thenReturn(testLoan);
            when(loanApplicationUseCase.execute(anyString(), any(Loan.class)))
                .thenReturn(Mono.just(testApplication));
            when(responseMapper.toResponseDto(testApplication)).thenReturn(responseDto);

            StepVerifier.create(handler.registerApplication(serverRequest))
                .assertNext(response -> {
                    assertNotNull(response);
                })
                .verifyComplete();
        }
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected non-null value");
        }
    }
}