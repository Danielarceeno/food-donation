package com.example.donation.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PontoArrecadacaoTest {

    @Test
    void builderAndGettersSetters() {
        ItemSolicitado item = ItemSolicitado.builder().id(7L).build();

        PontoArrecadacao ponto = PontoArrecadacao.builder()
            .id(30L)
            .endereco("Av. Central, 123")
            .item(item)
            .build();

        assertThat(ponto.getId()).isEqualTo(30L);
        assertThat(ponto.getEndereco()).isEqualTo("Av. Central, 123");
        assertThat(ponto.getItem()).isEqualTo(item);

        ponto.setEndereco("Rua Nova, 222");
        assertThat(ponto.getEndereco()).isEqualTo("Rua Nova, 222");
    }

    @Test
    void allArgsConstructorAndNoArgsConstructor() {
        PontoArrecadacao ponto = new PontoArrecadacao();
        ponto.setId(33L);
        assertThat(ponto.getId()).isEqualTo(33L);

        PontoArrecadacao full = new PontoArrecadacao(2L, "Rua Y", null);
        assertThat(full.getEndereco()).isEqualTo("Rua Y");
    }
}
