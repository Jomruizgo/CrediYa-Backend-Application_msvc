package com.crediya.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("loan_applications")
public class LoanApplicationEntity {
    
    @Id
    private String id;
    private String identityDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private String loanType;
    private String status;
    private LocalDateTime createdAt;

    public LoanApplicationEntity() {}

    public LoanApplicationEntity(String id, String identityDocument, BigDecimal amount, 
                               Integer termMonths, String loanType, String status, 
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

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
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
}