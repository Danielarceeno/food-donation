package com.example.donation.controller;

import com.example.donation.dto.UserRequestDTO;
import com.example.donation.dto.UserResponseDTO;
import com.example.donation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @Operation(summary = "Busca usuário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "ID não existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
        @PathVariable Long id
    ) {
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
        @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Operation(summary = "Remove usuário (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído"),
        @ApiResponse(responseCode = "404", description = "ID não existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Dados do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil retornado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> whoAmI(
        @AuthenticationPrincipal UserDetails ud
    ) {
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
        @RequestBody UserRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateProfile(ud.getUsername(), dto));
    }

    @Operation(summary = "Upload de avatar/logo do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avatar enviado"),
        @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping(path = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> uploadAvatar(
        @AuthenticationPrincipal UserDetails ud,
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(userService.uploadAvatar(ud.getUsername(), file));
    }

    @Operation(summary = "Lista todos os usuários (admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
