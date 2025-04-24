package com.example.donation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "itens_solicitados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSolicitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "instituicao_id")
    private User solicitante;

    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PontoArrecadacao> pontosArrecadacao = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}