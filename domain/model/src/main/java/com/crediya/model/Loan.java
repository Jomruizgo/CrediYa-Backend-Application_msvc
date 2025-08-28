package com.crediya.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Loan {
    private final BigDecimal amount;
    private final Integer termMonths;
    private final LoanType type;

    public Loan(BigDecimal amount, Integer termMonths, LoanType type) {
        this.amount = amount;
        this.termMonths = termMonths;
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public LoanType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(amount, loan.amount) &&
                Objects.equals(termMonths, loan.termMonths) &&
                type == loan.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, termMonths, type);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", termMonths=" + termMonths +
                ", type=" + type +
                '}';
    }
}