package com.example.donation.dto;

import com.example.donation.entity.Categoria;
import com.example.donation.entity.StatusDoacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoacaoResponseDTO {

    @Schema(description = "ID da doação", example = "100")
    private Long id;

    @Schema(description = "Título da doação", example = "Doação de Roupas")
    private String titulo;

    @Schema(description = "Descrição da doação", example = "Várias camisetas tamanho M")
    private String descricao;

    @Schema(description = "Categoria do item", example = "VESTUARIO")
    private Categoria categoria;

    @Schema(description = "Status atual da doação", example = "PENDENTE")
    private StatusDoacao status;

    @Schema(description = "E-mail do doador", example = "alice@example.com")
    private String doadorEmail;
}
