package com.example.donation.controller;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.service.DoacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Doações", description = "Endpoints para gerenciamento de doações")
@RestController
@RequestMapping(path = "/api/doacoes", produces = "application/json")
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService doacaoService;

    @Operation(summary = "Cria uma nova doação")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Doação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<DoacaoResponseDTO> create(
        @Valid @RequestBody DoacaoRequestDTO dto,
        @AuthenticationPrincipal UserDetails user
    ) {
        DoacaoResponseDTO created = doacaoService.create(dto, user.getUsername());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Listar minhas doações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<Page<DoacaoResponseDTO>> listMyDonations(
        @AuthenticationPrincipal UserDetails user,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Page<DoacaoResponseDTO> page = doacaoService.listByDoador(user.getUsername(), pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Listar todas as doações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('INSTITUICAO','ADMIN')")
    public ResponseEntity<Page<DoacaoResponseDTO>> listAll(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Page<DoacaoResponseDTO> page = doacaoService.listAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Confirma uma doação existente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Doação confirmada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<Void> confirmDonation(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user
    ) {
        doacaoService.confirm(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exclui uma doação")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Doação excluída"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Doação não encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOADOR','ADMIN')")
    public ResponseEntity<Void> deleteDonation(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user
    ) {
        doacaoService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
