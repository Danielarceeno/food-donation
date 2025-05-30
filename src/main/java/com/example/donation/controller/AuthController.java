package com.example.donation.controller;

import com.example.donation.dto.ForgotPasswordRequestDTO;
import com.example.donation.dto.LoginRequestDTO;
import com.example.donation.dto.ResetPasswordRequestDTO;
import com.example.donation.security.JwtTokenProvider;
import com.example.donation.service.PasswordResetService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Tag(name = "Autenticação", description = "Login e recuperação de senha")
@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Autentica usuário e retorna JWT")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de requisição",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "LoginExample",
                    summary = "Credenciais de login",
                    value   = "{\n" +
                        "  \"email\": \"alice@example.com\",\n" +
                        "  \"senha\": \"senha123\"\n" +
                        "}"
                )
            )
        )
        @Valid @RequestBody LoginRequestDTO dto
    ) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getSenha()
            )
        );
        String token = jwtTokenProvider.createToken(auth);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @Operation(summary = "Solicita e-mail de recuperação de senha")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado"),
        @ApiResponse(responseCode = "404", description = "E-mail não cadastrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de requisição",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "ForgotPasswordExample",
                    summary = "Solicitação de recuperação",
                    value   = "{ \"email\": \"alice@example.com\" }"
                )
            )
        )
        @Valid @RequestBody ForgotPasswordRequestDTO dto
    ) {
        passwordResetService.forgotPassword(dto);
        return ResponseEntity.ok("E-mail de recuperação enviado, verifique sua caixa de entrada.");
    }

    @Operation(summary = "Redefine a senha usando token enviado por e-mail")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Exemplo de requisição",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name    = "ResetPasswordExample",
                    summary = "Redefinição de senha",
                    value   = "{\n" +
                        "  \"token\": \"febfb408-1e94-4a65-98f3-19eb47d4e499\",\n" +
                        "  \"newPassword\": \"novaSenha123\"\n" +
                        "}"
                )
            )
        )
        @Valid @RequestBody ResetPasswordRequestDTO dto
    ) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}
