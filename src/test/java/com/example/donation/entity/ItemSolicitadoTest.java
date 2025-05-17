package com.example.donation.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSolicitadoTest {

    @Test
    void builderAndGettersSetters() {
        User user = User.builder().id(4L).build();
        LocalDateTime now = LocalDateTime.now();
        PontoArrecadacao ponto = PontoArrecadacao.builder().id(6L).endereco("Rua Z").build();

        ItemSolicitado item = ItemSolicitado.builder()
            .id(3L)
            .titulo("Cesta")
            .descricao("Cesta básica")
            .categoria(Categoria.ALIMENTO)
            .dataCriacao(now)
            .solicitante(user)
            .pontosArrecadacao(List.of(ponto))
            .build();

        assertThat(item.getId()).isEqualTo(3L);
        assertThat(item.getTitulo()).isEqualTo("Cesta");
        assertThat(item.getDescricao()).isEqualTo("Cesta básica");
        assertThat(item.getCategoria()).isEqualTo(Categoria.ALIMENTO);
        assertThat(item.getDataCriacao()).isEqualTo(now);
        assertThat(item.getSolicitante()).isEqualTo(user);
        assertThat(item.getPontosArrecadacao()).containsExactly(ponto);

        item.setTitulo("Roupas");
        assertThat(item.getTitulo()).isEqualTo("Roupas");
    }

    @Test
    void allArgsConstructorAndNoArgsConstructor() {
        ItemSolicitado item = new ItemSolicitado();
        item.setId(5L);
        assertThat(item.getId()).isEqualTo(5L);

        ItemSolicitado full = new ItemSolicitado(1L, "T", "D", Categoria.VESTUARIO, LocalDateTime.now(), null, List.of());
        assertThat(full.getTitulo()).isEqualTo("T");
    }

    @Test
    void prePersistShouldSetDataCriacao() {
        ItemSolicitado item = new ItemSolicitado();
        item.prePersist();
        assertThat(item.getDataCriacao()).isNotNull();
    }
}
