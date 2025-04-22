package com.example.donation.dto;

import com.example.donation.entity.UserType;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String nomeCompleto;
    private String email;
    private String senha;
    private UserType tipo;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String telefone;
    private String cnpj; // opcional para instituições
}