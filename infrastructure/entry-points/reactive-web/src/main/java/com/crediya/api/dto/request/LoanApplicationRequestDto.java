package com.crediya.api.dto.request;

import com.crediya.util.Constant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanApplicationRequestDto {
    
    @NotNull(message = Constant.DTO_USER_ID_REQUIRED)
    private Long userId;
    
    @NotBlank(message = Constant.DTO_IDENTITY_DOCUMENT_REQUIRED)
    private String identityDocument;
    
    @NotNull(message = Constant.DTO_AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = Constant.DTO_AMOUNT_MIN)
    private BigDecimal amount;
    
    @NotNull(message = Constant.DTO_TERM_REQUIRED)
    @Min(value = 1, message = Constant.DTO_TERM_MIN)
    private Integer termMonths;
    
    @NotNull(message = Constant.DTO_LOAN_TYPE_REQUIRED)
    private Long loanTypeId;

    public LoanApplicationRequestDto() {}

    public LoanApplicationRequestDto(Long userId, String identityDocument, BigDecimal amount, Integer termMonths, Long loanTypeId) {
        this.userId = userId;
        this.identityDocument = identityDocument;
        this.amount = amount;
        this.termMonths = termMonths;
        this.loanTypeId = loanTypeId;
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

    public Long getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(Long loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
}