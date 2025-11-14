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

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList(
            "https://*.ngrok-free.app",
            "https://*.ngrok.io",
            "https://*.zrok.io",
            "https://*.share.zrok.io",
            "http://localhost:*"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Necessário para enviar cookies, JWT no header, etc.
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private JwtAuthenticationFilter jwtFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService) {
            @Override
            public void doFilterInternal(
                jakarta.servlet.http.HttpServletRequest request,
                jakarta.servlet.http.HttpServletResponse response,
                jakarta.servlet.FilterChain filterChain)
                throws java.io.IOException, jakarta.servlet.ServletException {

                // libera preflight (OPTIONS)
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    response.setStatus(200);
                    filterChain.doFilter(request, response);
                    return;
                }

                super.doFilterInternal(request, response, filterChain);
            }
        };
    }

    // --------------------------------------------------------------------
    // 🔒 Configuração de segurança
    // --------------------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // público
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // ITENS
                .requestMatchers(HttpMethod.GET, "/api/itens/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/itens/**").hasRole("INSTITUICAO")
                .requestMatchers(HttpMethod.PUT, "/api/itens/**").hasRole("INSTITUICAO")
                .requestMatchers(HttpMethod.DELETE, "/api/itens/**").hasRole("INSTITUICAO")

                // DOAÇÕES
                .requestMatchers(HttpMethod.GET, "/api/doacoes/me").hasRole("DOADOR")
                .requestMatchers(HttpMethod.POST, "/api/doacoes").hasRole("DOADOR")
                .requestMatchers(HttpMethod.GET, "/api/doacoes").hasAnyRole("INSTITUICAO", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/doacoes/*").hasRole("DOADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/doacoes/*").hasAnyRole("DOADOR", "ADMIN")

                // PERFIL
                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()

                // ADMIN
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
