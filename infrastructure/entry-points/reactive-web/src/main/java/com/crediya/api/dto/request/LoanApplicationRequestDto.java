package com.crediya.api.dto.request;

import com.crediya.model.LoanType;
import com.crediya.util.Constant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanApplicationRequestDto {
    
    @NotBlank(message = Constant.DTO_IDENTITY_DOCUMENT_REQUIRED)
    private String identityDocument;
    
    @NotNull(message = Constant.DTO_AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = Constant.DTO_AMOUNT_MIN)
    private BigDecimal amount;
    
    @NotNull(message = Constant.DTO_TERM_REQUIRED)
    @Min(value = 1, message = Constant.DTO_TERM_MIN)
    private Integer termMonths;
    
    @NotNull(message = Constant.DTO_LOAN_TYPE_REQUIRED)
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