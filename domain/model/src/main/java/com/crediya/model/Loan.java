package com.crediya.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Loan {
    private final BigDecimal amount;
    private final Integer termMonths;
    private final Long loanTypeId;

    public Loan(BigDecimal amount, Integer termMonths, Long loanTypeId) {
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanTypeId = loanTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public Long getLoanTypeId() {
        return loanTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(amount, loan.amount) &&
                Objects.equals(termMonths, loan.termMonths) &&
                Objects.equals(loanTypeId, loan.loanTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, termMonths, loanTypeId);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", termMonths=" + termMonths +
                ", loanTypeId=" + loanTypeId +
                '}';
    }
}