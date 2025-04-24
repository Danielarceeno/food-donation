package com.example.donation.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ItemSolicitadoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String solicitanteNome;
    private String dataCriacao;
    private List<String> pontosArrecadacao;
    private Long doacoesRecebidas;
}