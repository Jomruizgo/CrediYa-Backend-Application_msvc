package com.crediya.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationReviewResponseDto {
    private String id;
    private String identityDocument;
    private String email;
    private String name;
    private BigDecimal amount;
    private Integer termMonths;
    private String loanTypeName;
    private BigDecimal interestRate;
    private String status;
    private BigDecimal baseSalary;
    private BigDecimal monthlyPaymentAmount;
    private BigDecimal approvedLoansMonthlyPayment;
    private LocalDateTime createdAt;

    public LoanApplicationReviewResponseDto() {}

    public LoanApplicationReviewResponseDto(String id, String identityDocument, String email,
                                          String name, BigDecimal amount, Integer termMonths,
                                          String loanTypeName, BigDecimal interestRate, String status,
                                          BigDecimal baseSalary, BigDecimal monthlyPaymentAmount,
                                          BigDecimal approvedLoansMonthlyPayment, LocalDateTime createdAt) {
        this.id = id;
        this.identityDocument = identityDocument;
        this.email = email;
        this.name = name;
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanTypeName = loanTypeName;
        this.interestRate = interestRate;
        this.status = status;
        this.baseSalary = baseSalary;
        this.monthlyPaymentAmount = monthlyPaymentAmount;
        this.approvedLoansMonthlyPayment = approvedLoansMonthlyPayment;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getMonthlyPaymentAmount() {
        return monthlyPaymentAmount;
    }

    public void setMonthlyPaymentAmount(BigDecimal monthlyPaymentAmount) {
        this.monthlyPaymentAmount = monthlyPaymentAmount;
    }

    public BigDecimal getApprovedLoansMonthlyPayment() {
        return approvedLoansMonthlyPayment;
    }

    public void setApprovedLoansMonthlyPayment(BigDecimal approvedLoansMonthlyPayment) {
        this.approvedLoansMonthlyPayment = approvedLoansMonthlyPayment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}