package com.crediya.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class LoanApplicationReview {
    private final String id;
    private final String identityDocument;
    private final String email;
    private final String name;
    private final BigDecimal amount;
    private final Integer termMonths;
    private final String loanTypeName;
    private final BigDecimal interestRate;
    private final ApplicationStatus status;
    private final BigDecimal baseSalary;
    private final BigDecimal monthlyPaymentAmount;
    private final BigDecimal approvedLoansMonthlyPayment;
    private final LocalDateTime createdAt;

    public LoanApplicationReview(String id, String identityDocument, String email, 
                               String name, BigDecimal amount, Integer termMonths, 
                               String loanTypeName, BigDecimal interestRate, 
                               ApplicationStatus status, BigDecimal baseSalary, 
                               BigDecimal monthlyPaymentAmount, BigDecimal approvedLoansMonthlyPayment, 
                               LocalDateTime createdAt) {
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

    public String getIdentityDocument() {
        return identityDocument;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public String getLoanTypeName() {
        return loanTypeName;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public BigDecimal getMonthlyPaymentAmount() {
        return monthlyPaymentAmount;
    }

    public BigDecimal getApprovedLoansMonthlyPayment() {
        return approvedLoansMonthlyPayment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplicationReview that = (LoanApplicationReview) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LoanApplicationReview{" +
                "id='" + id + '\'' +
                ", identityDocument='" + identityDocument + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", termMonths=" + termMonths +
                ", loanTypeName='" + loanTypeName + '\'' +
                ", interestRate=" + interestRate +
                ", status=" + status +
                ", baseSalary=" + baseSalary +
                ", monthlyPaymentAmount=" + monthlyPaymentAmount +
                ", approvedLoansMonthlyPayment=" + approvedLoansMonthlyPayment +
                ", createdAt=" + createdAt +
                '}';
    }
}