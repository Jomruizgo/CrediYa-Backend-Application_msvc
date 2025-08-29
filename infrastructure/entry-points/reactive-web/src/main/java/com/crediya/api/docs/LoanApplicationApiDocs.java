package com.crediya.api.docs;

import com.crediya.api.dto.request.LoanApplicationRequestDto;
import com.crediya.api.dto.response.LoanApplicationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Loan Applications", description = "Operations related to loan applications")
public class LoanApplicationApiDocs {

    @Operation(
        summary = "Register loan application",
        description = "Registers a new loan application in the system",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Loan application data",
            required = true,
            content = @Content(schema = @Schema(implementation = LoanApplicationRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Application registered successfully",
                content = @Content(schema = @Schema(implementation = LoanApplicationResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "422", description = "Business validation error")
        }
    )
    public void registerApplication() {}

    @Operation(
        summary = "Get application by ID",
        description = "Retrieves a specific loan application by its ID",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID of the loan application", 
            required = true,
            schema = @Schema(type = "string")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Application found",
                content = @Content(schema = @Schema(implementation = LoanApplicationResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Application not found")
        }
    )
    public void getApplicationById() {}
}