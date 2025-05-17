package com.example.donation.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ForgotPasswordRequestDTOTest {

    @Test
    void settersAndGetters() {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setEmail("reset@email.com");

        assertThat(dto.getEmail()).isEqualTo("reset@email.com");
    }
}
