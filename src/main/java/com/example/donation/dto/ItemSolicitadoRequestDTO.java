package com.example.donation.dto;

import lombok.Data;

@Data
public class ItemSolicitadoRequestDTO {
    private String titulo;
    private String descricao;
    private String categoria; // string igual ao nome da enum: HIGIENE, VESTUARIO…
}