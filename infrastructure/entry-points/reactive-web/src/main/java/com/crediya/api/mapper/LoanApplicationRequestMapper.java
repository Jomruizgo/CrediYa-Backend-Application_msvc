package com.crediya.api.mapper;

import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationRequestMapper {

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "termMonths", source = "termMonths")
    @Mapping(target = "type", source = "loanType")
    Loan toLoan(LoanApplicationRequestDto requestDto);
}