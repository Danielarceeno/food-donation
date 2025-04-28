package com.example.donation.service;

import com.example.donation.entity.ItemSolicitado;
import com.example.donation.repository.ItemSolicitadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledEmailService {

    private final ItemSolicitadoRepository itemRepo;
    private final JavaMailSender mailSender;

    @Value("${mail.schedule.cron}")
    private String cronExpression;

    // Executa no cron definido (diariamente às 08:00)
    @Scheduled(cron = "${mail.schedule.cron}")
    public void sendPendingItemsEmail() {
        // Extrai e-mails únicos de todas as solicitações
        List<String> emails = itemRepo.findAll().stream()
                .map(item -> item.getSolicitante().getEmail())
                .distinct()
                .collect(Collectors.toList());

        for (String email : emails) {
            List<ItemSolicitado> itens = itemRepo.findBySolicitanteEmail(email);
            if (itens.isEmpty()) continue;

            // Monta corpo do e-mail
            StringBuilder body = new StringBuilder();
            body.append("Olá!\n");
            body.append("Segue a lista de itens que você cadastrou:\n\n");
            itens.forEach(i -> body.append("- ")
                    .append(i.getTitulo())
                    .append(" : ")
                    .append(i.getDescricao())
                    .append("\n"));
            body.append("\nPor favor, verifique se já recebeu algum deles e atualize no sistema.\n");

            // Envia e-mail
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Lembrete: Itens pendentes de recepção");
            msg.setText(body.toString());
            mailSender.send(msg);
        }
    }
}
