package com.example.donation.controller;

import com.example.donation.dto.*;
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

    @PostMapping
    @PreAuthorize("hasRole('INSTITUICAO')")
    public ResponseEntity<ItemSolicitadoResponseDTO> create(
            @RequestBody ItemSolicitadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.create(dto, ud.getUsername()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemSolicitadoResponseDTO>> list(
            @RequestParam(value="search", required=false) String search
    ) {
        return ResponseEntity.ok(service.listAll(search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemSolicitadoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUICAO')")
    public ResponseEntity<ItemSolicitadoResponseDTO> update(
            @PathVariable Long id,
            @RequestBody ItemSolicitadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails ud
    ) {
        return ResponseEntity.ok(service.update(id, dto, ud.getUsername()));
    }
}
