package com.example.donation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ItemSolicitadoRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Categoria é obrigatória")
    private String categoria;

    /**
     * Pelo menos um endereço de ponto de arrecadação.
     * Cada elemento da lista também não pode ser vazio.
     */
    @NotEmpty(message = "É necessário pelo menos um ponto de arrecadação")
    private List<@NotBlank(message = "Endereço do ponto de arrecadação não pode ser vazio") String> pontosArrecadacao;
}
