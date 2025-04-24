package com.example.donation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pontos_arrecadacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoArrecadacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemSolicitado item;
}