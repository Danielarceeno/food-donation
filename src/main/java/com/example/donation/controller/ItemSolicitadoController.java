package com.example.donation.controller;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.service.ItemSolicitadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Itens Solicitados", description = "CRUD de solicitações de itens")
@RestController
@RequestMapping("/api/itens")
@RequiredArgsConstructor
public class ItemSolicitadoController {

    private final ItemSolicitadoService service;

    @Operation(summary = "Cria nova solicitação de item")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitação criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão (não é instituição)")
    })
    @PostMapping
    public ResponseEntity<ItemSolicitadoResponseDTO> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de criação de item solicitado",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "CreateItemSolicitadoExample",
                    summary = "Novo item solicitado",
                    value   = "{\n" +
                        "  \"titulo\": \"Leite NAN 1.0\",\n" +
                        "  \"descricao\": \"Leite comprado em farmácia para crianças\",\n" +
                        "  \"categoria\": \"ALIMENTO\",\n" +
                        "  \"pontosArrecadacao\": [\n" +
                        "    \"Rua A, 123\",\n" +
                        "    \"Rua B, 456\"\n" +
                        "  ]\n" +
                        "}"
                )
            )
        )
        @RequestBody ItemSolicitadoRequestDTO dto,
        @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.create(dto, ud.getUsername()));
    }

    @Operation(summary = "Lista todas as solicitações (com busca opcional)")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemSolicitadoResponseDTO>> list(
        @RequestParam(value = "search", required = false) String search
    ) {
        return ResponseEntity.ok(service.listAll(search));
    }

    @Operation(summary = "Busca uma solicitação por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitação encontrada"),
        @ApiResponse(responseCode = "404", description = "ID não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemSolicitadoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Atualiza uma solicitação existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitação atualizada"),
        @ApiResponse(responseCode = "403", description = "Não é instituição ou não dono"),
        @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemSolicitadoResponseDTO> update(
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de atualização de item solicitado",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "UpdateItemSolicitadoExample",
                    summary = "Atualização de item solicitado",
                    value   = "{\n" +
                        "  \"titulo\": \"Leite NAN 1.0 (atualizado)\",\n" +
                        "  \"descricao\": \"Leite para bebês até 1 ano\",\n" +
                        "  \"categoria\": \"ALIMENTO\",\n" +
                        "  \"pontosArrecadacao\": [\n" +
                        "    \"Rua C, 789\"\n" +
                        "  ]\n" +
                        "}"
                )
            )
        )
        @RequestBody ItemSolicitadoRequestDTO dto,
        @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.update(id, dto, ud.getUsername()));
    }

    @Operation(summary = "Exclui uma solicitação de item")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitação excluída"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUICAO')")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails ud
    ) {
        service.delete(id, ud.getUsername());
        return ResponseEntity.noContent().build();
    }
}
