package com.example.donation.controller;

import com.example.donation.dto.DoacaoRequestDTO;
import com.example.donation.dto.DoacaoResponseDTO;
import com.example.donation.service.DoacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doacoes")
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService doacaoService;

    @PostMapping
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<DoacaoResponseDTO> create(@RequestBody DoacaoRequestDTO dto,
                                                    @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(doacaoService.create(dto, user.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INSTITUICAO','ADMIN')")
    public ResponseEntity<List<DoacaoResponseDTO>> getAll() {
        return ResponseEntity.ok(doacaoService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTITUICAO','ADMIN')")
    public ResponseEntity<DoacaoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doacaoService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<DoacaoResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(doacaoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOADOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me/count")
    @PreAuthorize("hasRole('DOADOR')")
    public ResponseEntity<Long> countMyDonations(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(doacaoService.countByEmail(ud.getUsername()));
    }
}
