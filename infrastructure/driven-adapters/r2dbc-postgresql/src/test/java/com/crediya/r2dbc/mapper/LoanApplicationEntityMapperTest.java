package com.crediya.r2dbc.mapper;

import com.crediya.model.ApplicationStatus;
import com.crediya.model.Loan;
import com.crediya.model.LoanApplication;
import com.crediya.model.LoanType;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationEntityMapperTest {

    private LoanApplicationEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LoanApplicationEntityMapper.class);
    }

    @Test
    void shouldMapLoanApplicationToEntity() {
        Loan loan = new Loan(
            new BigDecimal("1000000.00"),
            12,
            LoanType.PERSONAL
        );
        
        LocalDateTime createdAt = LocalDateTime.now();
        LoanApplication application = new LoanApplication(
            "app-123",
            "12345678",
            loan,
            ApplicationStatus.PENDING_REVIEW,
            createdAt
        );

        LoanApplicationEntity entity = mapper.toEntity(application);

        assertNotNull(entity);
        assertEquals("app-123", entity.getId());
        assertEquals("12345678", entity.getIdentityDocument());
        assertEquals(new BigDecimal("1000000.00"), entity.getAmount());
        assertEquals(12, entity.getTermMonths());
        assertEquals("PERSONAL", entity.getLoanType());
        assertEquals("PENDING_REVIEW", entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void shouldMapEntityToLoanApplication() {
        LocalDateTime createdAt = LocalDateTime.now();
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setId("app-456");
        entity.setIdentityDocument("87654321");
        entity.setAmount(new BigDecimal("2500000.00"));
        entity.setTermMonths(24);
        entity.setLoanType("MORTGAGE");
        entity.setStatus("APPROVED");
        entity.setCreatedAt(createdAt);

        LoanApplication application = mapper.toDomain(entity);

        assertNotNull(application);
        assertEquals("app-456", application.getId());
        assertEquals("87654321", application.getIdentityDocument());
        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertEquals(createdAt, application.getCreatedAt());
        
        assertNotNull(application.getLoan());
        assertEquals(new BigDecimal("2500000.00"), application.getLoan().getAmount());
        assertEquals(24, application.getLoan().getTermMonths());
        assertEquals(LoanType.MORTGAGE, application.getLoan().getType());
    }

    @Test
    void shouldHandleAllLoanTypes() {
        for (LoanType loanType : LoanType.values()) {
            Loan loan = new Loan(new BigDecimal("1000000"), 12, loanType);
            LoanApplication application = new LoanApplication(
                "app-" + loanType.name(),
                "12345678",
                loan,
                ApplicationStatus.PENDING_REVIEW,
                LocalDateTime.now()
            );

            LoanApplicationEntity entity = mapper.toEntity(application);
            assertEquals(loanType.name(), entity.getLoanType());

            LoanApplication mappedBack = mapper.toDomain(entity);
            assertEquals(loanType, mappedBack.getLoan().getType());
        }
    }

    @Test
    void shouldHandleAllApplicationStatuses() {
        Loan loan = new Loan(new BigDecimal("1000000"), 12, LoanType.PERSONAL);
        
        for (ApplicationStatus status : ApplicationStatus.values()) {
            LoanApplication application = new LoanApplication(
                "app-" + status.name(),
                "12345678",
                loan,
                status,
                LocalDateTime.now()
            );

            LoanApplicationEntity entity = mapper.toEntity(application);
            assertEquals(status.name(), entity.getStatus());

            LoanApplication mappedBack = mapper.toDomain(entity);
            assertEquals(status, mappedBack.getStatus());
        }
    }

    @Test
    void shouldHandleNullLoanType() {
        Loan loanWithNullType = new Loan(new BigDecimal("1000000"), 12, null);
        LoanApplication application = new LoanApplication(
            "app-123",
            "12345678",
            loanWithNullType,
            ApplicationStatus.PENDING_REVIEW,
            LocalDateTime.now()
        );

        LoanApplicationEntity entity = mapper.toEntity(application);
        assertNull(entity.getLoanType());
    }

    @Test
    void shouldHandleNullApplicationStatus() {
        Loan loan = new Loan(new BigDecimal("1000000"), 12, LoanType.PERSONAL);
        LoanApplication application = new LoanApplication(
            "app-123",
            "12345678",
            loan,
            null,
            LocalDateTime.now()
        );

        LoanApplicationEntity entity = mapper.toEntity(application);
        assertNull(entity.getStatus());
    }

    @Test
    void shouldHandleRoundTripMapping() {
        Loan originalLoan = new Loan(
            new BigDecimal("3750000.75"),
            36,
            LoanType.AUTO
        );
        
        LocalDateTime createdAt = LocalDateTime.of(2023, 12, 25, 15, 30, 45);
        LoanApplication originalApplication = new LoanApplication(
            "app-round-trip",
            "999888777",
            originalLoan,
            ApplicationStatus.IN_EVALUATION,
            createdAt
        );

        LoanApplicationEntity entity = mapper.toEntity(originalApplication);
        LoanApplication roundTripApplication = mapper.toDomain(entity);

        assertEquals(originalApplication.getId(), roundTripApplication.getId());
        assertEquals(originalApplication.getIdentityDocument(), roundTripApplication.getIdentityDocument());
        assertEquals(originalApplication.getStatus(), roundTripApplication.getStatus());
        assertEquals(originalApplication.getCreatedAt(), roundTripApplication.getCreatedAt());
        
        assertEquals(originalLoan.getAmount(), roundTripApplication.getLoan().getAmount());
        assertEquals(originalLoan.getTermMonths(), roundTripApplication.getLoan().getTermMonths());
        assertEquals(originalLoan.getType(), roundTripApplication.getLoan().getType());
    }

    @Test
    void shouldHandleEntityWithNullStringValues() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setId("app-null-test");
        entity.setIdentityDocument("12345678");
        entity.setAmount(new BigDecimal("1000000"));
        entity.setTermMonths(12);
        entity.setLoanType(null);
        entity.setStatus(null);
        entity.setCreatedAt(LocalDateTime.now());

        LoanApplication application = mapper.toDomain(entity);

        assertEquals("app-null-test", application.getId());
        assertEquals("12345678", application.getIdentityDocument());
        assertNull(application.getStatus());
        assertNotNull(application.getLoan());
        assertEquals(new BigDecimal("1000000"), application.getLoan().getAmount());
        assertEquals(12, application.getLoan().getTermMonths());
    }

    @Test
    void shouldThrowExceptionWhenMappingInvalidLoanTypeString() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setId("app-invalid");
        entity.setIdentityDocument("12345678");
        entity.setAmount(new BigDecimal("1000000"));
        entity.setTermMonths(12);
        entity.setLoanType("INVALID_LOAN_TYPE");
        entity.setStatus("PENDING_REVIEW");
        entity.setCreatedAt(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(entity));
    }

    @Test
    void shouldThrowExceptionWhenMappingInvalidStatusString() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setId("app-invalid-status");
        entity.setIdentityDocument("12345678");
        entity.setAmount(new BigDecimal("1000000"));
        entity.setTermMonths(12);
        entity.setLoanType("PERSONAL");
        entity.setStatus("INVALID_STATUS");
        entity.setCreatedAt(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(entity));
    }
}