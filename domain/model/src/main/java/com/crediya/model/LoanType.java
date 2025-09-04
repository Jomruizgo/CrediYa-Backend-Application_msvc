package com.crediya.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class LoanType {
    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final Integer minTermMonths;
    private final Integer maxTermMonths;
    private final BigDecimal interestRate;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public LoanType(Long id, String name, String description, BigDecimal minAmount,
                   BigDecimal maxAmount, Integer minTermMonths, Integer maxTermMonths,
                   BigDecimal interestRate, Boolean isActive, LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.interestRate = interestRate;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public Integer getMinTermMonths() {
        return minTermMonths;
    }

    public Integer getMaxTermMonths() {
        return maxTermMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanType loanType = (LoanType) o;
        return Objects.equals(id, loanType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LoanType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", minTermMonths=" + minTermMonths +
                ", maxTermMonths=" + maxTermMonths +
                ", interestRate=" + interestRate +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}