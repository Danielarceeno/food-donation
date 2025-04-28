package com.example.donation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final JavaMailSender mailSender;

    @GetMapping("/api/test-email")
    public ResponseEntity<String> sendTestEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("daniel7ar@gmail.com");               // para si mesmo
        msg.setSubject("🚀 Teste SMTP Gmail");
        msg.setText("Se você recebeu isto, seu SMTP está OK!");
        mailSender.send(msg);
        return ResponseEntity.ok("E-mail de teste enviado com sucesso!");
    }
}
