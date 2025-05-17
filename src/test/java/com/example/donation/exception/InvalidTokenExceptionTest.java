package com.example.donation.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidTokenExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        InvalidTokenException ex = new InvalidTokenException("Token inválido");
        assertThat(ex.getMessage()).isEqualTo("Token inválido");
    }
}
