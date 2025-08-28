package com.crediya.api.dto.response;

import com.crediya.model.LoanType;
import com.crediya.model.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationResponseDto {
    private String id;
    private String identityDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private LoanType loanType;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public LoanApplicationResponseDto() {}

    public LoanApplicationResponseDto(String id, String identityDocument, BigDecimal amount, 
                                    Integer termMonths, LoanType loanType, ApplicationStatus status, 
                                    LocalDateTime createdAt) {
        this.id = id;
        this.identityDocument = identityDocument;
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanType = loanType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}