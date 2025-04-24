package com.example.donation.dto;

import lombok.Data;
import java.util.List;

@Data
public class ItemSolicitadoRequestDTO {
    private String titulo;
    private String descricao;
    private String categoria;
    private List<String> pontosArrecadacao;
}