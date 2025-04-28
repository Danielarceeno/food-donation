package com.example.donation.dto;

import lombok.Data;

@Data
public class DoacaoRequestDTO {
    private String titulo;
    private String descricao;
    private String categoria;
    private Long itemSolicitadoId;
    private String comentario;
}
