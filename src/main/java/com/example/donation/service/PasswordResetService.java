package com.example.donation.service;

import com.example.donation.dto.ForgotPasswordRequestDTO;
import com.example.donation.dto.ResetPasswordRequestDTO;
import com.example.donation.entity.PasswordResetToken;
import com.example.donation.entity.User;
import com.example.donation.exception.InvalidTokenException;
import com.example.donation.repository.PasswordResetTokenRepository;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.reset-password-url:http://localhost:8080/reset-password}")
    private String resetPasswordUrl;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long tokenValidityMillis;

    /**
     * Cria token e envia e-mail de recuperação.
     */
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Email não encontrado: " + dto.getEmail()));

        // Gera token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(tokenValidityMillis / 1000);

        PasswordResetToken prt = PasswordResetToken.builder()
            .token(token)
            .user(user)
            .expiryDate(expiry)
            .build();
        tokenRepository.save(prt);

        // Envia e-mail
        String link = resetPasswordUrl + "?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Recuperação de senha - DoaçãoApp");
        msg.setText("Para redefinir sua senha, clique no link abaixo:\n" + link + "\nEste link expira em 1 hora.");
        mailSender.send(msg);
    }

    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetToken prt = tokenRepository.findByToken(dto.getToken())
            .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));

        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(prt);
            throw new InvalidTokenException("Token expirado.");
        }

        User user = prt.getUser();
        user.setSenha(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Remove token
        tokenRepository.delete(prt);
    }
}
