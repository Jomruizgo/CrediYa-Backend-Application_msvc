package com.crediya.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LoanApplication {
    private final String id;
    private final Long userId;
    private final String identityDocument;
    private final Loan loan;
    private final ApplicationStatus status;
    private final LocalDateTime createdAt;

    public LoanApplication(String id, Long userId, String identityDocument, Loan loan, 
                          ApplicationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.identityDocument = identityDocument;
        this.loan = loan;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public Loan getLoan() {
        return loan;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", identityDocument='" + identityDocument + '\'' +
                ", loan=" + loan +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}