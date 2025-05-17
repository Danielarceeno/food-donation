package com.example.donation.service;

import com.example.donation.entity.ItemSolicitado;
import com.example.donation.entity.User;
import com.example.donation.repository.ItemSolicitadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScheduledEmailServiceTest {

    @Mock
    private ItemSolicitadoRepository itemRepo;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ScheduledEmailService scheduledEmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendPendingItemsEmail_deveEnviarEmailsApenasParaInstituicoesComItens() {
        // Usuário com 2 itens cadastrados
        User user1 = User.builder()
            .id(1L)
            .email("instituicao1@teste.com")
            .nomeCompleto("Instituição 1")
            .build();

        ItemSolicitado item1 = ItemSolicitado.builder()
            .id(11L)
            .titulo("Roupas de Inverno")
            .descricao("Agasalhos e cobertores")
            .solicitante(user1)
            .build();
        ItemSolicitado item2 = ItemSolicitado.builder()
            .id(12L)
            .titulo("Alimentos não perecíveis")
            .descricao("Arroz, feijão, etc")
            .solicitante(user1)
            .build();

        // Usuário sem item (não receberá e-mail)
        User user2 = User.builder()
            .id(2L)
            .email("instituicao2@teste.com")
            .nomeCompleto("Instituição 2")
            .build();

        // itemRepo.findAll()
        when(itemRepo.findAll()).thenReturn(List.of(item1, item2));
        // itemRepo.findBySolicitanteEmail()
        when(itemRepo.findBySolicitanteEmail("instituicao1@teste.com"))
            .thenReturn(List.of(item1, item2));
        when(itemRepo.findBySolicitanteEmail("instituicao2@teste.com"))
            .thenReturn(List.of());

        scheduledEmailService.sendPendingItemsEmail();

        // Captura todos os emails enviados
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, atLeast(0)).send(captor.capture());

        // Verifica que só foi enviado para instituicao1
        List<SimpleMailMessage> sentEmails = captor.getAllValues();
        assertThat(sentEmails).hasSize(1);
        SimpleMailMessage sentMsg = sentEmails.get(0);
        assertThat(sentMsg.getTo()).containsExactly("instituicao1@teste.com");
        assertThat(sentMsg.getText()).contains("Roupas de Inverno");
        assertThat(sentMsg.getText()).contains("Alimentos não perecíveis");

        // Garante que nenhum e-mail foi enviado para user2
        boolean emailEnviadoParaUser2 = sentEmails.stream()
            .anyMatch(msg -> msg.getTo() != null && List.of(msg.getTo()).contains("instituicao2@teste.com"));
        assertThat(emailEnviadoParaUser2).isFalse();
    }
}
