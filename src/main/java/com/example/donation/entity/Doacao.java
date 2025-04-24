package com.example.donation.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "doacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String categoria;
    private String status; // Ex: PENDENTE, CONFIRMADA

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario; // Relacionamento com doador

    @ManyToOne
    @JoinColumn(name = "item_solicitado_id")
    private ItemSolicitado itemSolicitado; // a qual solicitação responde
}