package com.example.donation.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void shouldCreateApiErrorWithFields() {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, "Erro", List.of("fail1", "fail2"));
        assertThat(error.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error.getMessage()).isEqualTo("Erro");
        assertThat(error.getErrors()).containsExactly("fail1", "fail2");
        assertThat(error.getTimestamp()).isNotNull();
    }
}
