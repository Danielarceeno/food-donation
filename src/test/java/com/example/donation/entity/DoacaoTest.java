package com.example.donation.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoacaoTest {

    @Test
    void builderAndGettersSetters() {
        User user = User.builder().id(1L).email("doador@email.com").build();
        ItemSolicitado item = ItemSolicitado.builder().id(2L).build();

        Doacao doacao = Doacao.builder()
            .id(10L)
            .titulo("Roupa")
            .descricao("Roupa de inverno")
            .categoria(Categoria.VESTUARIO)
            .status(StatusDoacao.PENDENTE)
            .usuario(user)
            .itemSolicitado(item)
            .build();

        assertThat(doacao.getId()).isEqualTo(10L);
        assertThat(doacao.getTitulo()).isEqualTo("Roupa");
        assertThat(doacao.getDescricao()).isEqualTo("Roupa de inverno");
        assertThat(doacao.getCategoria()).isEqualTo(Categoria.VESTUARIO);
        assertThat(doacao.getStatus()).isEqualTo(StatusDoacao.PENDENTE);
        assertThat(doacao.getUsuario()).isEqualTo(user);
        assertThat(doacao.getItemSolicitado()).isEqualTo(item);

        doacao.setTitulo("Alimento");
        assertThat(doacao.getTitulo()).isEqualTo("Alimento");
    }

    @Test
    void allArgsConstructorAndNoArgsConstructor() {
        Doacao doacao = new Doacao();
        doacao.setId(1L);
        assertThat(doacao.getId()).isEqualTo(1L);

        Doacao full = new Doacao(2L, "Teste", "Desc", Categoria.ALIMENTO, StatusDoacao.CONFIRMADA, null, null);
        assertThat(full.getId()).isEqualTo(2L);
        assertThat(full.getStatus()).isEqualTo(StatusDoacao.CONFIRMADA);
    }
}
