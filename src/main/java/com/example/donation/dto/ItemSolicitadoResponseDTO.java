package com.example.donation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemSolicitadoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String solicitanteNome;
    private String dataCriacao; // ex: "2025-04-24T15:30:00"
}