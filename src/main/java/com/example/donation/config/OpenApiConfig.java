package com.example.donation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração geral do OpenAPI (Swagger) para DoaçãoApp.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "API DoaçãoApp",
        version = "1.1",
        description = "API para gerenciamento de doações, itens solicitados e usuários",
        contact = @Contact(name = "Equipe DoaçãoApp", email = "suporte@doacaoapp.com"),
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor Local (DEV)"),
        @Server(url = "https://api.doacaoapp.com", description = "Servidor de Produção")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@Configuration
public class OpenApiConfig {

}
