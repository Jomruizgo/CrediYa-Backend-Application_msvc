package com.crediya.api.mapper;

import com.crediya.api.dto.response.LoanApplicationReviewResponseDto;
import com.crediya.model.LoanApplicationReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationReviewResponseMapper {

    @Mapping(target = "status", expression = "java(loanApplicationReview.getStatus().name())")
    LoanApplicationReviewResponseDto toResponseDto(LoanApplicationReview loanApplicationReview);
}