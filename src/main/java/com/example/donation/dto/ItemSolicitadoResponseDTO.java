package com.example.donation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemSolicitadoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String solicitanteNome;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCriacao;

    private List<String> pontosArrecadacao;
    private Long doacoesRecebidas;
}