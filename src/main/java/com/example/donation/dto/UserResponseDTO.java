package com.example.donation.dto;

import com.example.donation.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String avatarUrl;
}
