package com.example.donation.config;

import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import com.example.donation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureAdminExists() {
        boolean exists = userRepo.existsByTipo(UserType.ADMIN);
        if (exists) {
            log.info("Já existe pelo menos um ADMIN. Pulando criação automática.");
            return;
        }

        User admin = User.builder()
            .nomeCompleto("Administrador DoacaoApp")
            .email(adminEmail)
            .senha(encoder.encode(adminPassword))
            .tipo(UserType.ADMIN)
            .cidade("São Paulo")
            .bairro("")
            .rua("")
            .numero("")
            .telefone("")
            .cnpj("")
            .avatarUrl(null)
            .build();

        userRepo.save(admin);
        log.info("Usuário ADMIN criado automaticamente: {}", adminEmail);
    }
}
