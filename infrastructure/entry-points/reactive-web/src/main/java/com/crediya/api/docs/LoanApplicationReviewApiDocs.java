package com.crediya.api.docs;

import com.crediya.api.dto.response.PageResponseDto;
import com.crediya.api.dto.response.LoanApplicationReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Loan Application Reviews", description = "Operations related to loan application reviews")
public class LoanApplicationReviewApiDocs {

    @Operation(
        summary = "Get applications for review",
        description = "Retrieves paginated loan applications with optional status filtering. Supports dynamic filtering by application status. Only accessible by SELLER role.",
        parameters = {
            @Parameter(
                name = "page", 
                in = ParameterIn.QUERY, 
                description = "Page number (0-based)", 
                required = false,
                schema = @Schema(type = "integer", defaultValue = "0")
            ),
            @Parameter(
                name = "size", 
                in = ParameterIn.QUERY, 
                description = "Number of items per page", 
                required = false,
                schema = @Schema(type = "integer", defaultValue = "20")
            ),
            @Parameter(
                name = "sortBy", 
                in = ParameterIn.QUERY, 
                description = "Field to sort by", 
                required = false,
                schema = @Schema(type = "string", defaultValue = "createdAt")
            ),
            @Parameter(
                name = "sortOrder", 
                in = ParameterIn.QUERY, 
                description = "Sort order (ASC or DESC)", 
                required = false,
                schema = @Schema(type = "string", defaultValue = "DESC")
            ),
            @Parameter(
                name = "status", 
                in = ParameterIn.QUERY, 
                description = "Filter by application status. Supports multiple values separated by comma. Valid values: PENDING_REVIEW, IN_EVALUATION, APPROVED, REJECTED, DISBURSED, MANUAL_REVIEW, PAID", 
                required = false,
                schema = @Schema(
                    type = "string", 
                    example = "APPROVED,DISBURSED",
                    allowableValues = {"PENDING_REVIEW", "IN_EVALUATION", "APPROVED", "REJECTED", "DISBURSED", "MANUAL_REVIEW", "PAID"}
                )
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Applications retrieved successfully",
                content = @Content(schema = @Schema(implementation = PageResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have SELLER role")
        }
    )
    public void getApplicationsForReview() {}
}