package com.example.donation.controller;

import com.example.donation.dto.ForgotPasswordRequestDTO;
import com.example.donation.dto.LoginRequestDTO;
import com.example.donation.dto.ResetPasswordRequestDTO;
import com.example.donation.security.JwtTokenProvider;
import com.example.donation.service.PasswordResetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_deveRetornarJwt() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@mail.com");
        dto.setSenha("123456");

        Authentication auth = new UsernamePasswordAuthenticationToken("user@mail.com", "123456", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.createToken(auth)).thenReturn("jwt-teste");

        var response = authController.login(dto);

        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().get("token")).isEqualTo("jwt-teste");
    }

    @Test
    void forgotPassword_deveChamarServico() {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setEmail("a@a.com");

        var response = authController.forgotPassword(dto);

        verify(passwordResetService, times(1)).forgotPassword(dto);
        assertThat(response.getBody()).contains("E-mail de recuperação");
    }

    @Test
    void resetPassword_deveChamarServico() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setToken("tok");
        dto.setNewPassword("senhaNova");

        var response = authController.resetPassword(dto);

        verify(passwordResetService, times(1)).resetPassword(dto);
        assertThat(response.getBody()).contains("Senha redefinida");
    }
}
