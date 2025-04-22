package com.example.donation.controller;

import com.example.donation.dto.ItemSolicitadoRequestDTO;
import com.example.donation.dto.ItemSolicitadoResponseDTO;
import com.example.donation.service.ItemSolicitadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
@RequiredArgsConstructor
public class ItemSolicitadoController {

    private final ItemSolicitadoService service;

    /**
     * Cria um novo item solicitado.
     */
    @PostMapping
    public ResponseEntity<ItemSolicitadoResponseDTO> create(
            @RequestBody ItemSolicitadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails ud
    ) {
        ItemSolicitadoResponseDTO resp = service.create(dto, ud.getUsername());
        return ResponseEntity.ok(resp);
    }

    /**
     * Lista todos os itens. Pode receber ?search=parteDoTitulo
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemSolicitadoResponseDTO>> list(
            @RequestParam(value = "search", required = false) String search
    ) {
        return ResponseEntity.ok(service.listAll(search));
    }
}
