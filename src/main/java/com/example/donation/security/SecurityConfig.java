package com.example.donation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ─── ENDPOINTS PÚBLICOS ────────────────────────────────────────────────
                .requestMatchers(
                    "/",
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // permitir cadastro de usuário (doadores / instituições)
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                // ─── ITENS SOLICITADOS ────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/itens/**").authenticated()
                .requestMatchers(HttpMethod.POST,   "/api/itens/**").hasRole("INSTITUICAO")
                .requestMatchers(HttpMethod.PUT,    "/api/itens/**").hasRole("INSTITUICAO")
                .requestMatchers(HttpMethod.DELETE, "/api/itens/**").hasRole("INSTITUICAO")

                // ─── DOAÇÕES ──────────────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/doacoes/me").hasRole("DOADOR")
                .requestMatchers(HttpMethod.POST,   "/api/doacoes").hasRole("DOADOR")
                .requestMatchers(HttpMethod.GET,    "/api/doacoes").hasAnyRole("INSTITUICAO","ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/doacoes/*").hasRole("DOADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/doacoes/*").hasAnyRole("DOADOR","ADMIN")

                // ─── PERFIL (usuário logado) ─────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,  "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/api/users/me").authenticated()

                // ─── ADMIN ONLY (listagem completa e remoção) ────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                // ─── QUALQUER OUTRA ────────────────────────────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
