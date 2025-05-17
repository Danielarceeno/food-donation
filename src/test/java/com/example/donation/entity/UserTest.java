package com.example.donation.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builderAndGettersSetters() {
        User user = User.builder()
            .id(100L)
            .nomeCompleto("Fulano da Silva")
            .email("fulano@teste.com")
            .senha("senha123")
            .tipo(UserType.INSTITUICAO)
            .cidade("Cidade")
            .bairro("Bairro")
            .rua("Rua X")
            .numero("123")
            .telefone("99998888")
            .cnpj("99.999.999/0001-99")
            .avatarUrl("/img/1.jpg")
            .build();

        assertThat(user.getId()).isEqualTo(100L);
        assertThat(user.getNomeCompleto()).isEqualTo("Fulano da Silva");
        assertThat(user.getEmail()).isEqualTo("fulano@teste.com");
        assertThat(user.getSenha()).isEqualTo("senha123");
        assertThat(user.getTipo()).isEqualTo(UserType.INSTITUICAO);
        assertThat(user.getCidade()).isEqualTo("Cidade");
        assertThat(user.getBairro()).isEqualTo("Bairro");
        assertThat(user.getRua()).isEqualTo("Rua X");
        assertThat(user.getNumero()).isEqualTo("123");
        assertThat(user.getTelefone()).isEqualTo("99998888");
        assertThat(user.getCnpj()).isEqualTo("99.999.999/0001-99");
        assertThat(user.getAvatarUrl()).isEqualTo("/img/1.jpg");

        user.setNomeCompleto("Novo Nome");
        assertThat(user.getNomeCompleto()).isEqualTo("Novo Nome");
    }

    @Test
    void allArgsConstructorAndNoArgsConstructor() {
        User user = new User();
        user.setId(10L);
        assertThat(user.getId()).isEqualTo(10L);

        User full = new User(1L, "N", "E", "S", UserType.DOADOR,
            "C", "B", "R", "N", "T", "CNPJ", "URL");
        assertThat(full.getNomeCompleto()).isEqualTo("N");
        assertThat(full.getTipo()).isEqualTo(UserType.DOADOR);
    }
}
