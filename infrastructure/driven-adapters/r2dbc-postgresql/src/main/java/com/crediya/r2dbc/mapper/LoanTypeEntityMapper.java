package com.crediya.r2dbc.mapper;

import com.crediya.model.LoanType;
import com.crediya.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanTypeEntityMapper {
    LoanTypeEntity toEntity(LoanType loanType);
    LoanType toDomain(LoanTypeEntity entity);
}