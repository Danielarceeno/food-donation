package com.example.donation.dto;

import com.example.donation.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoacaoRequestDTO {

    @Schema(description = "Título da doação", example = "Doação de Roupas")
    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @Schema(description = "Descrição da doação", example = "Várias camisetas tamanho M")
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @Schema(description = "Categoria do item", example = "VESTUARIO")
    @NotNull(message = "Categoria é obrigatória")
    private Categoria categoria;

    @Schema(description = "ID do item solicitado que originou esta doação", example = "42")
    @NotNull(message = "ID do item solicitado é obrigatório")
    private Long itemSolicitadoId;

    @Schema(description = "Comentário adicional", example = "Posso entregar amanhã cedo")
    private String comentario;
}
