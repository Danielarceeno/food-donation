package com.example.donation.dto;

import com.example.donation.entity.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String nomeCompleto;
    private String email;
    private UserType tipo;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String telefone;
    private String cnpj;
}