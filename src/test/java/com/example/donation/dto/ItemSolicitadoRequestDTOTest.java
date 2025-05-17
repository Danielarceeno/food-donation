package com.example.donation.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSolicitadoRequestDTOTest {

    @Test
    void settersAndGetters() {
        ItemSolicitadoRequestDTO dto = new ItemSolicitadoRequestDTO();
        dto.setTitulo("Campanha de alimentos");
        dto.setDescricao("Doações para o Natal");
        dto.setCategoria("ALIMENTO");
        dto.setPontosArrecadacao(List.of("Rua X, 123", "Rua Y, 456"));

        assertThat(dto.getTitulo()).isEqualTo("Campanha de alimentos");
        assertThat(dto.getDescricao()).isEqualTo("Doações para o Natal");
        assertThat(dto.getCategoria()).isEqualTo("ALIMENTO");
        assertThat(dto.getPontosArrecadacao()).containsExactly("Rua X, 123", "Rua Y, 456");
    }
}
