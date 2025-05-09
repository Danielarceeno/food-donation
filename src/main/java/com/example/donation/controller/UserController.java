package com.example.donation.controller;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Usuários", description = "CRUD de usuários e perfil")
@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Cria um novo usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de criação de usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "CreateUserExample",
                    summary = "Novo usuário",
                    value   = "{\n" +
                        "  \"nomeCompleto\": \"João Silva\",\n" +
                        "  \"email\": \"joao@example.com\",\n" +
                        "  \"senha\": \"senha123\",\n" +
                        "  \"tipo\": \"DOADOR\",\n" +
                        "  \"cidade\": \"São Paulo\",\n" +
                        "  \"bairro\": \"Centro\",\n" +
                        "  \"rua\": \"Rua X\",\n" +
                        "  \"numero\": \"100\",\n" +
                        "  \"telefone\": \"11999999999\",\n" +
                        "  \"cnpj\": \"\"\n" +
                        "}"
                )
            )
        )
        @Valid @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @Operation(summary = "Busca usuário por ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Atualiza dados de usuário (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "404", description = "ID não existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de atualização de usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "UpdateUserExample",
                    summary = "Alteração de usuário",
                    value   = "{\n" +
                        "  \"nomeCompleto\": \"João S. Atualizado\",\n" +
                        "  \"email\": \"joao_new@example.com\",\n" +
                        "  \"senha\": \"novaSenha123\",\n" +
                        "  \"tipo\": \"INSTITUICAO\",\n" +
                        "  \"cidade\": \"Orleans\",\n" +
                        "  \"bairro\": \"Centro\",\n" +
                        "  \"rua\": \"Rua Y\",\n" +
                        "  \"numero\": \"200\",\n" +
                        "  \"telefone\": \"11888888888\",\n" +
                        "  \"cnpj\": \"12.345.678/0001-00\"\n" +
                        "}"
                )
            )
        )
        @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Operation(summary = "Dados do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil retornado")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> whoAmI(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.findByEmail(ud.getUsername()));
    }

    @Operation(summary = "Atualiza perfil do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateMe(
        @AuthenticationPrincipal UserDetails ud,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de atualização de perfil",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "UpdateMeExample",
                    summary = "Alteração de perfil",
                    value   = "{\n" +
                        "  \"nomeCompleto\": \"Alice Maria\",\n" +
                        "  \"email\": \"alice_new@example.com\",\n" +
                        "  \"senha\": \"senhaSegura\",\n" +
                        "  \"cidade\": \"Florianópolis\",\n" +
                        "  \"bairro\": \"Trindade\",\n" +
                        "  \"rua\": \"Rua Z\",\n" +
                        "  \"numero\": \"300\",\n" +
                        "  \"telefone\": \"11977777777\",\n" +
                        "  \"cnpj\": \"\"\n" +
                        "}"
                )
            )
        )
        @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateProfile(ud.getUsername(), dto));
    }

    @Operation(summary = "Upload de avatar/logo do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Avatar enviado")
    @PostMapping(path = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> uploadAvatar(
        @AuthenticationPrincipal UserDetails ud,
        @RequestPart("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(userService.uploadAvatar(ud.getUsername(), file));
    }

    @Operation(summary = "Lista todos os usuários (admin)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
