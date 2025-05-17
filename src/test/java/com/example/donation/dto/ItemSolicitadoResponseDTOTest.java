package com.example.donation.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSolicitadoResponseDTOTest {

    @Test
    void builderAndGetters() {
        LocalDateTime data = LocalDateTime.now();
        ItemSolicitadoResponseDTO dto = ItemSolicitadoResponseDTO.builder()
            .id(5L)
            .titulo("Cestas")
            .descricao("Cestas de alimentos")
            .categoria("ALIMENTO")
            .solicitanteNome("Maria")
            .dataCriacao(data)
            .pontosArrecadacao(List.of("Rua Z, 999"))
            .doacoesRecebidas(3L)
            .build();

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getTitulo()).isEqualTo("Cestas");
        assertThat(dto.getDescricao()).isEqualTo("Cestas de alimentos");
        assertThat(dto.getCategoria()).isEqualTo("ALIMENTO");
        assertThat(dto.getSolicitanteNome()).isEqualTo("Maria");
        assertThat(dto.getDataCriacao()).isEqualTo(data);
        assertThat(dto.getPontosArrecadacao()).containsExactly("Rua Z, 999");
        assertThat(dto.getDoacoesRecebidas()).isEqualTo(3L);
    }
}
