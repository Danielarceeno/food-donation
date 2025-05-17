package com.example.donation.service;

import com.example.donation.dto.ForgotPasswordRequestDTO;
import com.example.donation.dto.ResetPasswordRequestDTO;
import com.example.donation.entity.PasswordResetToken;
import com.example.donation.entity.User;
import com.example.donation.repository.PasswordResetTokenRepository;
import com.example.donation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService service;

    private User user;
    private ForgotPasswordRequestDTO forgotDto;
    private ResetPasswordRequestDTO resetDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup @Value properties via ReflectionTestUtils (se necessário)
        // (Você pode remover se setar no construtor ou setar @Value manualmente)
        org.springframework.test.util.ReflectionTestUtils.setField(service, "resetPasswordUrl", "http://localhost/reset-password");
        org.springframework.test.util.ReflectionTestUtils.setField(service, "tokenValidityMillis", 3600000L);

        user = User.builder()
            .id(1L)
            .email("user@email.com")
            .nomeCompleto("Usuário Teste")
            .senha("senhaAntiga")
            .build();

        forgotDto = new ForgotPasswordRequestDTO();
        forgotDto.setEmail("user@email.com");
    }

    @Test
    void forgotPassword_DeveGerarTokenESalvarEEnviarEmail() {
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        service.forgotPassword(forgotDto);

        // Verifica se token foi salvo
        verify(tokenRepository).save(any(PasswordResetToken.class));
        // Verifica se o e-mail foi enviado
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(emailCaptor.capture());
        SimpleMailMessage message = emailCaptor.getValue();
        assertThat(message.getTo()).containsExactly("user@email.com");
        assertThat(message.getSubject()).contains("Recuperação de senha");
        assertThat(message.getText()).contains("http://localhost/reset-password?token=");
    }

    @Test
    void forgotPassword_UsuarioNaoExiste_DeveLancarExcecao() {
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setEmail("naoexiste@email.com");

        assertThatThrownBy(() -> service.forgotPassword(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email não encontrado");
    }

    @Test
    void resetPassword_TokenValido_DeveAlterarSenhaRemoverToken() {
        String novoHash = "novaSenhaHash";
        when(passwordEncoder.encode("novaSenha123")).thenReturn(novoHash);

        PasswordResetToken prt = PasswordResetToken.builder()
            .token("abc-token")
            .expiryDate(LocalDateTime.now().plusMinutes(20))
            .user(user)
            .build();

        when(tokenRepository.findByToken("abc-token")).thenReturn(Optional.of(prt));

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken("abc-token");
        dto.setNewPassword("novaSenha123");

        service.resetPassword(dto);

        // Verifica se a senha foi alterada e usuário salvo
        assertThat(user.getSenha()).isEqualTo(novoHash);
        verify(tokenRepository).delete(prt);
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_TokenExpirado_DeveLancarExcecao() {
        PasswordResetToken prt = PasswordResetToken.builder()
            .token("exp-token")
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .user(user)
            .build();

        when(tokenRepository.findByToken("exp-token")).thenReturn(Optional.of(prt));

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken("exp-token");
        dto.setNewPassword("novaSenha");

        assertThatThrownBy(() -> service.resetPassword(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Token expirado");

        verify(tokenRepository).delete(prt); // Token expirado deve ser removido
    }

    @Test
    void resetPassword_TokenNaoExiste_DeveLancarExcecao() {
        when(tokenRepository.findByToken("invalido")).thenReturn(Optional.empty());

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken("invalido");
        dto.setNewPassword("qualquer");

        assertThatThrownBy(() -> service.resetPassword(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Token inválido ou expirado");
    }
}
