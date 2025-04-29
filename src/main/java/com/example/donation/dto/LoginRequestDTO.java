package com.example.donation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Precisa ser um email válido")
    private String email;

    @Schema(description = "Senha do usuário", example = "senhaSegura123")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
