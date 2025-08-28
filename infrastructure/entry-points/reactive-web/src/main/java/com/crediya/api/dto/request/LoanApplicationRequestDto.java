package com.crediya.api.dto.request;

import com.crediya.model.LoanType;

import java.math.BigDecimal;

public class LoanApplicationRequestDto {
    private String identityDocument;
    private BigDecimal amount;
    private Integer termMonths;
    private LoanType loanType;

    public LoanApplicationRequestDto() {}

    public LoanApplicationRequestDto(String identityDocument, BigDecimal amount, Integer termMonths, LoanType loanType) {
        this.identityDocument = identityDocument;
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanType = loanType;
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
}