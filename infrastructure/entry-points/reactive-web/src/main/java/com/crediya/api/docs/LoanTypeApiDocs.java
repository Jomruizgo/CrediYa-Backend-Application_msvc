package com.crediya.api.docs;

import com.crediya.model.LoanType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Loan Types", description = "Operations related to loan types")
public class LoanTypeApiDocs {

    @Operation(
        summary = "Get all active loan types",
        description = "Retrieves all active loan types available in the system",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "List of loan types retrieved successfully",
                content = @Content(schema = @Schema(implementation = LoanType[].class))
            )
        }
    )
    public void getAllActiveLoanTypes() {}

    @Operation(
        summary = "Get loan type by ID",
        description = "Retrieves a specific loan type by its ID",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID of the loan type", 
            required = true,
            schema = @Schema(type = "integer", format = "int64")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Loan type found",
                content = @Content(schema = @Schema(implementation = LoanType.class))
            ),
            @ApiResponse(responseCode = "404", description = "Loan type not found")
        }
    )
    public void getLoanTypeById() {}
}