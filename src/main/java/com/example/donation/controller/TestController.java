package com.example.donation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para testar se o backend está no ar.
 */
@Tag(
    name = "Status",
    description = "Endpoints de verificação de saúde e status da API"
)
@RestController
public class TestController {

    @Operation(
        summary = "Verifica se o backend está no ar",
        description = "Retorna uma mensagem simples indicando que o serviço está funcionando"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Backend operacional"
    )
    @GetMapping(
        path = "/",
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> home() {
        // Aqui você pode até retornar um JSON se preferir, mas mantive texto simples
        return ResponseEntity
            .ok("Backend DoacaoApp está funcionando!");
    }
}
