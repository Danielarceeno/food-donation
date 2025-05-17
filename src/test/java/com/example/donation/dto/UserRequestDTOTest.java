package com.example.donation.dto;

import com.example.donation.entity.UserType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestDTOTest {

    @Test
    void settersAndGetters() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNomeCompleto("Test User");
        dto.setEmail("testuser@email.com");
        dto.setSenha("abc123");
        dto.setTipo(UserType.INSTITUICAO);
        dto.setCidade("CidadeY");
        dto.setBairro("BairroZ");
        dto.setRua("Rua W");
        dto.setNumero("200");
        dto.setTelefone("11999999988");
        dto.setCnpj("22.222.222/0001-11");

        assertThat(dto.getNomeCompleto()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("testuser@email.com");
        assertThat(dto.getSenha()).isEqualTo("abc123");
        assertThat(dto.getTipo()).isEqualTo(UserType.INSTITUICAO);
        assertThat(dto.getCidade()).isEqualTo("CidadeY");
        assertThat(dto.getBairro()).isEqualTo("BairroZ");
        assertThat(dto.getRua()).isEqualTo("Rua W");
        assertThat(dto.getNumero()).isEqualTo("200");
        assertThat(dto.getTelefone()).isEqualTo("11999999988");
        assertThat(dto.getCnpj()).isEqualTo("22.222.222/0001-11");
    }
}
