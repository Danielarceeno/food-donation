package com.example.donation.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        EmailNotFoundException ex = new EmailNotFoundException("foo@email.com");
        assertThat(ex.getMessage()).isEqualTo("E-mail não cadastrado: foo@email.com");
    }
}
