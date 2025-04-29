# DonationApp – Backend Spring Boot

[![Java 17](https://img.shields.io/badge/Java-17-blue)](https://adoptium.net/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green)](https://spring.io/projects/spring-boot)  
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📦 Visão Geral

APIs RESTful para gerenciar usuários, perfis, solicitações de itens e doações. Suporta dois perfis de usuário:

- **DOADOR**: faz doações e consulta suas próprias doações  
- **INSTITUIÇÃO**: solicita itens, gerencia solicitações e visualiza doações recebidas  

Também há papel **ADMIN** para operações administrativas (listar/deletar qualquer usuário).

## 🚀 Tecnologias

- **Java 17**  
- **Spring Boot 3.4.4**  
- **Spring Security** (JWT)  
- **Hibernate JPA**  
- **MapStruct** (DTO ↔︎ Entity)  
- **Lombok**  
- **Swagger / OpenAPI 3** via Springdoc  
- **Spring Mail** (envio de e-mail / recuperação de senha)  
- **PostgreSQL** (prod) / **H2** (dev)  
- **Docker & Docker Compose**  

---

## 🔧 Pré-requisitos

- Java 17+  
- Maven 3.6+  
- Docker & Docker Compose (opcional)  

---

## ⚙️ Configuração de ambiente

Crie um arquivo `.env` na raiz do projeto com estas variáveis:

```properties
# → Banco de dados
DB_NAME=doacao_db
DB_USER=postgres
DB_PASSWORD=123

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${DB_NAME}
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}

# → JWT
JWT_SECRET_KEY=sua_chave_secreta
JWT_TOKEN_VALIDITY=3600000

# → Upload de Avatares
FILE_UPLOAD_DIR=uploads

# → E-mail / SMTP
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=seu_email@gmail.com
SPRING_MAIL_PASSWORD=sua_senha_email
MAIL_SCHEDULE_CRON=0 0 8 * * ?

# → URL do frontend (reset de senha)
APP_FRONTEND_RESET_PASSWORD_URL=http://localhost:3000/reset-password
```

Ou defina essas propriedades em `application.yml` / `application.properties` conforme seu ambiente.

---

## 🏃‍♂️ Como rodar

### Local com Maven

```bash
git clone https://github.com/seu-org/donation-backend.git
cd donation-backend

# build + download dependências
mvn clean package

# roda aplicação
java -jar target/donation-0.0.1-SNAPSHOT.jar
```

### Com Docker Compose

```bash
# certifica-se de ter o .env configurado
docker-compose up --build -d
```

- **API** exposta em `http://localhost:8080`  
- **Swagger UI** em `http://localhost:8080/swagger-ui/index.html`  
- **OpenAPI JSON** em `http://localhost:8080/v3/api-docs`  

---

## 🔐 Fluxo de autenticação

1. **Cadastro**  
   - `POST /api/users`  
   - Body JSON com `tipo`: `DOADOR` ou `INSTITUICAO` (admin só via DB / seed)  

2. **Login**  
   - `POST /api/auth/login`  
   - Recebe `{ "token": "eyJ..." }`  

3. **Usar JWT**  
   - Header em todas as chamadas autenticadas:  
     ```
     Authorization: Bearer <token>
     ```

---

## 📑 Endpoints Principais

### Autenticação

| Método | Rota                             | Descrição                        |
| ------ | -------------------------------- | -------------------------------- |
| POST   | `/api/auth/login`                | Login (body: `{ email, senha }`) |
| POST   | `/api/auth/forgot-password`      | Envia e-mail de reset (body: `{ email }`) |
| POST   | `/api/auth/reset-password`       | Redefine senha (body: `{ token, newPassword }`) |

### Usuários

| Método | Rota                        | Permissão      | Descrição                   |
| ------ | --------------------------- | -------------- | --------------------------- |
| POST   | `/api/users`                | público        | Cria conta (DOADOR/INSTITUICAO) |
| GET    | `/api/users/{id}`           | ADMIN          | Busca usuário por ID        |
| GET    | `/api/users/me`             | autenticado    | Perfil do usuário logado    |
| PUT    | `/api/users/{id}`           | ADMIN          | Atualiza qualquer usuário   |
| PUT    | `/api/users/me`             | autenticado    | Atualiza próprio perfil     |
| DELETE | `/api/users/{id}`           | ADMIN          | Remove usuário              |
| POST   | `/api/users/me/avatar`      | autenticado    | Faz upload de avatar (form-data) |

### Itens Solicitados

| Método | Rota               | Permissão       | Descrição                          |
| ------ | ------------------ | --------------- | ---------------------------------- |
| GET    | `/api/itens`       | autenticado     | Lista todas as solicitações (opcional search) |
| GET    | `/api/itens/{id}`  | autenticado     | Detalha uma solicitação            |
| POST   | `/api/itens`       | INSTITUICAO     | Cria nova solicitação              |
| PUT    | `/api/itens/{id}`  | INSTITUICAO     | Atualiza solicitação               |
| DELETE | `/api/itens/{id}`  | INSTITUICAO     | Exclui solicitação                 |

### Doações

| Método | Rota                   | Permissão                 | Descrição                          |
| ------ | ---------------------- | ------------------------- | ---------------------------------- |
| GET    | `/api/doacoes/me`      | DOADOR                    | Lista minhas doações               |
| POST   | `/api/doacoes`         | DOADOR                    | Cria nova doação                   |
| GET    | `/api/doacoes`         | INSTITUIÇÃO, ADMIN        | Lista todas as doações recebidas   |
| PUT    | `/api/doacoes/{id}`    | DOADOR                    | Confirma/atualiza doação           |
| DELETE | `/api/doacoes/{id}`    | DOADOR (próprio) ou ADMIN | Remove doação                      |

### Utilitários

| Método | Rota                        | Descrição                                    |
| ------ | --------------------------- | -------------------------------------------- |
| GET    | `/`                         | Health-check (retorna texto simples)         |
| GET    | `/api/test-email`           | Envia e-mail de teste via SMTP               |

---

## 📬 Agendamento de lembretes

O bean `ScheduledEmailService` dispara toda manhã (cron: `${MAIL_SCHEDULE_CRON}`) um e-mail para cada instituição com a lista de itens pendentes.

---

## 💡 Dicas para desenvolvimento

- Use o **Swagger UI** para testar rapidamente todos os endpoints.  
- Habilite o **H2 Console** em dev (`spring.h2.console.enabled=true`) se precisar inspecionar o banco em memória.  
- Para testes de segurança, utilize `spring-security-test` e anote seus testes com `@WithMockUser(roles="DOADOR")` ou `roles="INSTITUICAO"`.  

---

## 🤝 Contribuição

1. Faça um _fork_ deste repositório  
2. Crie uma branch de feature: `git checkout -b feature/nome-da-feature`  
3. Commit: `git commit -m "feat: descrição sucinta"`  
4. Push: `git push origin feature/nome-da-feature`  
5. Abra um _Pull Request_  

---

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

