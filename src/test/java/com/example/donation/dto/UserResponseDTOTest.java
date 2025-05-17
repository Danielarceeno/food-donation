package com.example.donation.dto;

import com.example.donation.entity.UserType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseDTOTest {

    @Test
    void shouldCreateUserResponseDTO_WithBuilderAndGettersAndSetters() {
        // Usando o builder
        UserResponseDTO dto = UserResponseDTO.builder()
            .id(1L)
            .nomeCompleto("João Silva")
            .email("joao@example.com")
            .tipo(UserType.DOADOR)
            .cidade("São Paulo")
            .bairro("Centro")
            .rua("Rua X")
            .numero("100")
            .telefone("11999999999")
            .cnpj("12.345.678/0001-00")
            .avatarUrl("/avatar.png")
            .build();

        // Verifica getters
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNomeCompleto()).isEqualTo("João Silva");
        assertThat(dto.getEmail()).isEqualTo("joao@example.com");
        assertThat(dto.getTipo()).isEqualTo(UserType.DOADOR);
        assertThat(dto.getCidade()).isEqualTo("São Paulo");
        assertThat(dto.getBairro()).isEqualTo("Centro");
        assertThat(dto.getRua()).isEqualTo("Rua X");
        assertThat(dto.getNumero()).isEqualTo("100");
        assertThat(dto.getTelefone()).isEqualTo("11999999999");
        assertThat(dto.getCnpj()).isEqualTo("12.345.678/0001-00");
        assertThat(dto.getAvatarUrl()).isEqualTo("/avatar.png");

        // Usando setters
        dto.setNomeCompleto("Maria Silva");
        assertThat(dto.getNomeCompleto()).isEqualTo("Maria Silva");
    }

    @Test
    void shouldAllowNoArgsConstructorAndSetters() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(2L);
        dto.setNomeCompleto("Carlos Lima");
        dto.setEmail("carlos@example.com");
        dto.setTipo(UserType.ADMIN);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNomeCompleto()).isEqualTo("Carlos Lima");
        assertThat(dto.getEmail()).isEqualTo("carlos@example.com");
        assertThat(dto.getTipo()).isEqualTo(UserType.ADMIN);
    }
}
