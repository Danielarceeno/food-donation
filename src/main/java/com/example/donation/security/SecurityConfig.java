package com.example.donation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService) {
        this.jwtTokenProvider   = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/", "/api/auth/**", "/api/users", "/swagger-ui/**", "/v3/api-docs/**", "/api/auth/forgot-password",    // <— liberei aqui
                                "/api/auth/reset-password")
                        .permitAll()

                        // lista itens
                        .requestMatchers(HttpMethod.GET, "/api/itens/**").authenticated()

                        // CRUD itens (só instituição)
                        .requestMatchers(HttpMethod.POST, "/api/itens/**").hasRole("INSTITUICAO")
                        .requestMatchers(HttpMethod.PUT,  "/api/itens/**").hasRole("INSTITUICAO")
                        .requestMatchers(HttpMethod.DELETE, "/api/itens/**").hasRole("INSTITUICAO")

                        // listar minhas doações (só DOADOR)
                        .requestMatchers(HttpMethod.GET, "/api/doacoes/me").hasRole("DOADOR")
                        // criar doação (só DOADOR)
                        .requestMatchers(HttpMethod.POST, "/api/doacoes").hasRole("DOADOR")
                        // consultam restante das doações (instituição/admin)
                        .requestMatchers(HttpMethod.GET,    "/api/doacoes/**").hasAnyRole("INSTITUICAO","ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/doacoes/**").hasRole("DOADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/doacoes/**").hasAnyRole("DOADOR","ADMIN")

                        // perfil
                        .requestMatchers(HttpMethod.GET,  "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/users/me").authenticated()

                        // qualquer outra
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