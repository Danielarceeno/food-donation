package com.example.donation.security;

import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import com.example.donation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Test
    void shouldLoadUserByUsername() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repo);

        User user = User.builder()
            .email("me@x.com")
            .senha("123")
            .tipo(UserType.DOADOR)
            .build();

        when(repo.findByEmail("me@x.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("me@x.com");

        assertThat(details.getUsername()).isEqualTo("me@x.com");
        assertThat(details.getPassword()).isEqualTo("123");
        assertThat(details.getAuthorities()).extracting("authority")
            .contains("ROLE_DOADOR");
    }

    @Test
    void shouldThrowIfUserNotFound() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repo);

        when(repo.findByEmail("none@x.com")).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> service.loadUserByUsername("none@x.com"));
    }
}
