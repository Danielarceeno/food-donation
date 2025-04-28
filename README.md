# Donation API - Backend Spring Boot

## Descrição
API REST para gerenciamento de doações de itens

## Tecnologias
- Java 17
- Spring Boot 3.x
- Spring Security (JWT)
- Hibernate/JPA
- MapStruct
- Lombok
- PostgreSQL (produção) / H2 (desenvolvimento)
- Spring Mail (SMTP Gmail)
- Swagger / OpenAPI 3
- Docker / Docker Compose

## Pré-requisitos
- Java 17 instalado
- Maven instalado
- Docker e Docker Compose (opcional)
- Conta de email para SMTP (Gmail ou outro)

## Configuração de ambiente
Crie um arquivo `.env` ou configure as propriedades em `application.properties`/`application.yml`:

```properties
# Banco de dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/doacao_db
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha

# JWT
SECURITY_JWT_TOKEN_SECRET-KEY=sua_chave_secreta
SECURITY_JWT_TOKEN_EXPIRE-LENGTH=3600000

# Upload de arquivos
FILE_UPLOAD_DIR=uploads/avatars

# Email (SMTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=seu_email@gmail.com
SPRING_MAIL_PASSWORD=sua_senha_email
MAIL_SCHEDULE_CRON=0 0 8 * * ?

# Frontend (para link de reset de senha)
APP_FRONTEND_RESET-PASSWORD-URL=http://localhost:3000/reset-password
```

## Build e execução

### Local (Maven)
```bash
mvn clean package
java -jar target/*.jar
```

### Docker
```bash
docker build -t donation-app:1.0 .
docker run --env-file .env -p 8080:8080 donation-app:1.0
```

### Docker Compose
```yaml
services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    # carrega todas as chaves do seu .env
    env_file:
      - .env
    environment:
      # Sobrescreve (se necessário) ou repete apenas as específicas
      - SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
      - SPRING_JPA_HIBERNATE_DDL_AUTO=${SPRING_JPA_HIBERNATE_DDL_AUTO}
      - SPRING_JPA_DATABASE_PLATFORM=${SPRING_JPA_DATABASE_PLATFORM}
      - SECURITY_JWT_TOKEN_SECRET_KEY=${JWT_SECRET_KEY}
      - SECURITY_JWT_TOKEN_EXPIRE_LENGTH=${JWT_TOKEN_VALIDITY}

      # Email/Scheduler
      - SPRING_MAIL_HOST=${SPRING_MAIL_HOST}
      - SPRING_MAIL_PORT=${SPRING_MAIL_PORT}
      - SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
      - SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
      - SPRING_MAIL_SMTP_AUTH=${SPRING_MAIL_SMTP_AUTH}
      - SPRING_MAIL_SMTP_STARTTLS=${SPRING_MAIL_SMTP_STARTTLS}
      - MAIL_SCHEDULE_CRON=${MAIL_SCHEDULE_CRON}

    depends_on:
      db:
        condition: service_healthy
    networks:
      - app-network

  db:
    image: postgres:17.4-alpine
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USER}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER}"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - app-network

volumes:
  db-data:

networks:
  app-network:
    driver: bridge
```
Em seguida:
```bash
docker-compose up -d
```

## Endpoints principais

### Geral
- `GET /` — Verifica se o servidor está rodando

### Autenticação
- `POST /api/auth/login` — Login (body: `{ "email": "", "senha": "" }`)
- `POST /api/auth/forgot-password` — Solicita reset de senha (body: `{ "email": "" }`)
- `POST /api/auth/reset-password` — Redefine senha (body: `{ "token": "", "newPassword": "" }`)

### Usuários
- `POST /api/users` — Cria novo usuário
- `GET /api/users/{id}` — Detalha usuário por ID
- `PUT /api/users/{id}` — Atualiza usuário
- `DELETE /api/users/{id}` — Remove usuário
- `GET /api/users/me` — Perfil do usuário logado
- `PUT /api/users/me` — Atualiza perfil do usuário
- `POST /api/users/me/avatar` — Upload de avatar (multipart/form-data)

### Itens Solicitados
- `POST /api/itens` — Cria item (somente INSTITUIÇÃO)
- `GET /api/itens` — Lista itens (autenticado)
- `GET /api/itens/{id}` — Detalha item
- `PUT /api/itens/{id}` — Atualiza item (INSTITUIÇÃO)
- `DELETE /api/itens/{id}` — Exclui item (INSTITUIÇÃO)

### Doações
- `GET /api/doacoes/me` — Lista minhas doações (DOADOR)
- `POST /api/doacoes` — Cria doação (DOADOR)
- `GET /api/doacoes` — Lista todas doações (INSTITUIÇÃO, ADMIN)
- `PUT /api/doacoes/{id}` — Atualiza status ou comentário (DOADOR)
- `DELETE /api/doacoes/{id}` — Remove doação (DOADOR, ADMIN)

### Testes e utilitários
- `GET /api/test-email` — Envia email de teste SMTP
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI Docs: `http://localhost:8080/v3/api-docs`

## Agendamento de lembretes
O serviço `ScheduledEmailService` envia diariamente às 08:00 um email com a lista de itens pendentes para cada instituição, conforme cron configurado em `MAIL_SCHEDULE_CRON`.

## Contribuição
1. Fork deste repositório
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Faça commits claros (`git commit -m "Adiciona ..."`)
4. Envie pull request

## Licença
Este projeto está licenciado sob a [MIT License](LICENSE).

