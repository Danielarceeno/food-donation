package com.example.donation.dto;

import com.example.donation.entity.Categoria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoacaoRequestDTOTest {

    @Test
    void gettersAndSetters() {
        DoacaoRequestDTO dto = new DoacaoRequestDTO();
        dto.setTitulo("Roupas");
        dto.setDescricao("Doação de roupas usadas");
        dto.setCategoria(Categoria.VESTUARIO);
        dto.setItemSolicitadoId(7L);
        dto.setComentario("Entrega na sexta-feira");

        assertThat(dto.getTitulo()).isEqualTo("Roupas");
        assertThat(dto.getDescricao()).isEqualTo("Doação de roupas usadas");
        assertThat(dto.getCategoria()).isEqualTo(Categoria.VESTUARIO);
        assertThat(dto.getItemSolicitadoId()).isEqualTo(7L);
        assertThat(dto.getComentario()).isEqualTo("Entrega na sexta-feira");
    }
}
