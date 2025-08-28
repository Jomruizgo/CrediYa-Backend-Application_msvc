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

@Tag(name = "Loan Applications", description = "Operaciones relacionadas con solicitudes de préstamos")
public class LoanApplicationApiDocs {

    @Operation(
        summary = "Registrar solicitud de préstamo",
        description = "Registra una nueva solicitud de préstamo en el sistema",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la solicitud de préstamo",
            required = true,
            content = @Content(schema = @Schema(implementation = LoanApplicationRequestDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Solicitud registrada exitosamente",
                content = @Content(schema = @Schema(implementation = LoanApplicationResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "422", description = "Error de validación de negocio")
        }
    )
    public void registerApplication() {}

    @Operation(
        summary = "Obtener solicitud por ID",
        description = "Obtiene una solicitud de préstamo específica por su ID",
        parameters = @Parameter(
            name = "id", 
            in = ParameterIn.PATH, 
            description = "ID de la solicitud de préstamo", 
            required = true,
            schema = @Schema(type = "string")
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Solicitud encontrada",
                content = @Content(schema = @Schema(implementation = LoanApplicationResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
        }
    )
    public void getApplicationById() {}
}