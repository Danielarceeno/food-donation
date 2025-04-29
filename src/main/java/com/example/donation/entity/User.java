package com.example.donation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomeCompleto;
    @Column(unique = true)
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING)
    private UserType tipo;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String telefone;
    private String cnpj;
    private String avatarUrl;
}
