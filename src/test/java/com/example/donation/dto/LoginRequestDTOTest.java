package com.example.donation.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDTOTest {

    @Test
    void settersAndGetters() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("login@email.com");
        dto.setSenha("senha123");

        assertThat(dto.getEmail()).isEqualTo("login@email.com");
        assertThat(dto.getSenha()).isEqualTo("senha123");
    }
}
