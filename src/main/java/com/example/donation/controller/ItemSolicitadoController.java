package com.example.donation.controller;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.entity.Categoria;
import com.example.donation.service.ItemSolicitadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Tag(name = "Itens Solicitados", description = "CRUD e busca paginada de solicitações de itens")
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
    @PreAuthorize("hasRole('INSTITUICAO')")
    public ResponseEntity<ItemSolicitadoResponseDTO> create(
        @Valid @RequestBody ItemSolicitadoRequestDTO dto,
        @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.create(dto, ud.getUsername()));
    }

    @Operation(summary = "Lista itens com filtros, paginação e ordenação")
    @ApiResponse(responseCode = "200", description = "Lista paginada de itens")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ItemSolicitadoResponseDTO>> list(
        @RequestParam(value = "search", required = false) String title,
        @RequestParam(value = "categoria", required = false) Categoria categoria,
        @RequestParam(value = "dataFrom", required = false)
        @DateTimeFormat(iso = DATE_TIME) LocalDateTime dataFrom,
        @RequestParam(value = "dataTo", required = false)
        @DateTimeFormat(iso = DATE_TIME) LocalDateTime dataTo,
        @RequestParam(value = "cidade", required = false) String cidade,
        @PageableDefault(size = 10, sort = "dataCriacao") Pageable pageable
    ) {
        Page<ItemSolicitadoResponseDTO> page = service.search(
            title, categoria, dataFrom, dataTo, cidade, pageable
        );
        return ResponseEntity.ok(page);
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
    @PreAuthorize("hasRole('INSTITUICAO') and @itemSolicitadoService.isOwner(#id, authentication.name)")
    public ResponseEntity<ItemSolicitadoResponseDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ItemSolicitadoRequestDTO dto,
        @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.update(id, dto, ud.getUsername()));
    }

    @Operation(summary = "Exclui uma solicitação de item")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitação excluída"),
        @ApiResponse(responseCode = "403", description = "Não é instituição ou não dono"),
        @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUICAO') and @itemSolicitadoService.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails ud
    ) {
        service.delete(id, ud.getUsername());
        return ResponseEntity.noContent().build();
    }
}
