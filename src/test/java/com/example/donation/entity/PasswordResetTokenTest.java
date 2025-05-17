package com.example.donation.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetTokenTest {

    @Test
    void builderAndGettersSetters() {
        User user = User.builder().id(8L).build();
        LocalDateTime exp = LocalDateTime.now().plusHours(1);

        PasswordResetToken token = PasswordResetToken.builder()
            .id(20L)
            .token("abc123")
            .expiryDate(exp)
            .user(user)
            .build();

        assertThat(token.getId()).isEqualTo(20L);
        assertThat(token.getToken()).isEqualTo("abc123");
        assertThat(token.getExpiryDate()).isEqualTo(exp);
        assertThat(token.getUser()).isEqualTo(user);

        token.setToken("xyz789");
        assertThat(token.getToken()).isEqualTo("xyz789");
    }

    @Test
    void allArgsConstructorAndNoArgsConstructor() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("tok");
        assertThat(token.getToken()).isEqualTo("tok");

        PasswordResetToken full = new PasswordResetToken(1L, "tok2", LocalDateTime.now(), null);
        assertThat(full.getToken()).isEqualTo("tok2");
    }
}
