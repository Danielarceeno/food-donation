package com.example.donation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para testar e verificar o envio de e-mail via SMTP.
 */
@Tag(
    name = "E-mail",
    description = "Endpoints para envio e teste de e-mails via SMTP"
)
@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final JavaMailSender mailSender;

    @Operation(
        summary = "Envia um e-mail de teste",
        description = "Dispara um e-mail simples para o destinatário configurado no método, validando a configuração SMTP"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "E-mail de teste enviado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Falha ao enviar o e-mail de teste")
    })
    @GetMapping(
        path = "/api/test-email",
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> sendTestEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("daniel7ar@gmail.com");               // seu e-mail de teste
        msg.setSubject("🚀 Teste SMTP Gmail");
        msg.setText("Se você recebeu isto, seu SMTP está OK!");
        mailSender.send(msg);
        return ResponseEntity
            .ok("E-mail de teste enviado com sucesso!");
    }
}
