package com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("loan_types")
public class LoanTypeEntity {
    
    @Id
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("description")
    private String description;
    
    @Column("min_amount")
    private BigDecimal minAmount;
    
    @Column("max_amount")
    private BigDecimal maxAmount;
    
    @Column("min_term_months")
    private Integer minTermMonths;
    
    @Column("max_term_months")
    private Integer maxTermMonths;
    
    @Column("interest_rate")
    private BigDecimal interestRate;
    
    @Column("is_active")
    private Boolean isActive;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}