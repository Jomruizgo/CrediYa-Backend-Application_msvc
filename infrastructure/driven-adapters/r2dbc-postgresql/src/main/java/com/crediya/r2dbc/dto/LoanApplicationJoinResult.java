package com.crediya.r2dbc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationJoinResult {
    private String id;
    private Long userId;
    private String identityDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private String status;
    private LocalDateTime createdAt;
    private String loanTypeName;
    private BigDecimal interestRate;

    public LoanApplicationJoinResult() {}

    public LoanApplicationJoinResult(String id, Long userId, String identityDocument, BigDecimal amount,
                                   Integer termMonths, String status, LocalDateTime createdAt,
                                   String loanTypeName, BigDecimal interestRate) {
        this.id = id;
        this.userId = userId;
        this.identityDocument = identityDocument;
        this.amount = amount;
        this.termMonths = termMonths;
        this.status = status;
        this.createdAt = createdAt;
        this.loanTypeName = loanTypeName;
        this.interestRate = interestRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLoanTypeName() {
        return loanTypeName;
    }

    public void setLoanTypeName(String loanTypeName) {
        this.loanTypeName = loanTypeName;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}