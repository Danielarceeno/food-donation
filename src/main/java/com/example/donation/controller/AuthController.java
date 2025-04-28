package com.example.donation.controller;

import com.example.donation.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.donation.dto.ForgotPasswordRequestDTO;
import com.example.donation.dto.ResetPasswordRequestDTO;
import com.example.donation.service.PasswordResetService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.get("email"), authRequest.get("senha"))
        );

        String token = jwtTokenProvider.createToken(authentication);

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO dto) {
        passwordResetService.forgotPassword(dto);
        return ResponseEntity.ok("E-mail de recuperação enviado, verifique sua caixa de entrada.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}