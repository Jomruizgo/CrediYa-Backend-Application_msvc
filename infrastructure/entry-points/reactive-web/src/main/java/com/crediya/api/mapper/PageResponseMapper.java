package com.crediya.api.mapper;

import com.crediya.api.dto.response.PageResponseDto;
import com.crediya.model.Page;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

    default <T, R> PageResponseDto<R> toPageResponseDto(Page<T> page, List<R> content) {
        return new PageResponseDto<>(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getPageNumber(),
                page.getPageSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}