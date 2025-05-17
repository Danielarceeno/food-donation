package com.example.donation.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResetPasswordRequestDTOTest {

    @Test
    void settersAndGetters() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken("token-123");
        dto.setNewPassword("novaSenha!");

        assertThat(dto.getToken()).isEqualTo("token-123");
        assertThat(dto.getNewPassword()).isEqualTo("novaSenha!");
    }
}
