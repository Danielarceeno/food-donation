package com.example.donation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoacaoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String status;
    private String doadorEmail;
}
