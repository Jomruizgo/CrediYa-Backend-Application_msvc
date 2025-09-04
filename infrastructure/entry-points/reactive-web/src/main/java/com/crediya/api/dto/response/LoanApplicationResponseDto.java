package com.crediya.api.dto.response;

import com.crediya.model.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationResponseDto {
    private String id;
    private String identityDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private Long loanTypeId;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public LoanApplicationResponseDto() {}

    public LoanApplicationResponseDto(String id, String identityDocument, BigDecimal amount, 
                                    Integer termMonths, Long loanTypeId, ApplicationStatus status, 
                                    LocalDateTime createdAt) {
        this.id = id;
        this.identityDocument = identityDocument;
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanTypeId = loanTypeId;
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

    public Long getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(Long loanTypeId) {
        this.loanTypeId = loanTypeId;
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