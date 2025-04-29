package com.example.donation.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa um erro padronizado retornado pela API.
 */
@Data
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime timestamp;

    /**
     * Construtor que inicializa timestamp automaticamente
     */
    public ApiError(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}