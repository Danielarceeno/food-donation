package com.example.donation.dto;

import com.example.donation.entity.Categoria;
import com.example.donation.entity.StatusDoacao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoacaoResponseDTOTest {

    @Test
    void builderAndGetters() {
        DoacaoResponseDTO dto = DoacaoResponseDTO.builder()
            .id(9L)
            .titulo("Alimentos")
            .descricao("Doação de cestas básicas")
            .categoria(Categoria.ALIMENTO)
            .status(StatusDoacao.PENDENTE)
            .doadorEmail("doa@email.com")
            .build();

        assertThat(dto.getId()).isEqualTo(9L);
        assertThat(dto.getTitulo()).isEqualTo("Alimentos");
        assertThat(dto.getDescricao()).isEqualTo("Doação de cestas básicas");
        assertThat(dto.getCategoria()).isEqualTo(Categoria.ALIMENTO);
        assertThat(dto.getStatus()).isEqualTo(StatusDoacao.PENDENTE);
        assertThat(dto.getDoadorEmail()).isEqualTo("doa@email.com");
    }
}
