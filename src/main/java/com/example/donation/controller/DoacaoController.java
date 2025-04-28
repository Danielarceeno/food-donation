package com.example.donation.controller;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.service.DoacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/doacoes", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService doacaoService;

    /**
     * Cria uma nova doação vinculada a um item existente
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DoacaoResponseDTO> create(
            @RequestBody DoacaoRequestDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        DoacaoResponseDTO resp = doacaoService.create(dto, user.getUsername());
        return ResponseEntity.ok(resp);
    }
}