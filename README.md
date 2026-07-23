# Ride Profit - Backend

Backend da plataforma de controle financeiro para motorista de aplicativo com veiculo eletrico.

## Stack

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

## Executar localmente

### 1. Subir o banco de dados

```bash
docker compose up -d
```

### 2. Compilar o projeto

```bash
mvn clean compile
```

### 3. Executar a aplicacao

```bash
mvn spring-boot:run
```

A API estara disponivel em `http://localhost:8080/api`.

## Usuario inicial

O sistema cria automaticamente um tenant, um usuario e um veiculo padrao:

- Email: `motorista@rideprofit.com`
- Senha: `rideprofit2026`
- Veiculo: BYD Dolphin (2024)

## Endpoints principais

### Autenticacao
- `POST /api/auth/login`
- `POST /api/auth/refresh`

### Configuracoes
- `GET /api/configuracoes/tarifa-energia`
- `PUT /api/configuracoes/tarifa-energia`

### Dashboard
- `GET /api/dashboard?periodo=DIARIO|SEMANAL|MENSAL|ANUAL|PERSONALIZADO&referencia=2026-07-17&inicio=2026-07-01&fim=2026-07-31`
- `GET /api/dashboard/financeiro?data=2026-07-17`
- `GET /api/dashboard/semanal?dataReferencia=2026-07-17`
- `GET /api/dashboard/mensal?mesReferencia=2026-07`
- `GET /api/dashboard/despesas-por-categoria?inicio=2026-07-01&fim=2026-07-31`

### Categorias de Despesa
- `GET /api/categorias-despesa`
- `POST /api/categorias-despesa`
- `PUT /api/categorias-despesa/{id}`
- `DELETE /api/categorias-despesa/{id}` (desativacao logica)

### Origens, Categorias de Saida e Formas de Pagamento
- `GET|POST /api/origens`
- `PUT|DELETE /api/origens/{id}`
- `GET|POST /api/categorias-saida`
- `PUT|DELETE /api/categorias-saida/{id}`
- `GET|POST /api/formas-pagamento`
- `PUT|DELETE /api/formas-pagamento/{id}`

### Perfil
- `GET /api/perfil`
- `PUT /api/perfil`
- `PUT /api/perfil/senha`

### Receitas
- `POST /api/receitas`
- `PUT /api/receitas/{id}`
- `GET /api/receitas?inicio=2026-07-01&fim=2026-07-31`
- `DELETE /api/receitas/{id}` (exclusao logica)

### Despesas
- `POST /api/despesas`
- `PUT /api/despesas/{id}`
- `GET /api/despesas?inicio=2026-07-01&fim=2026-07-31`
- `GET /api/despesas/{id}/nota-fiscal`
- `DELETE /api/despesas/{id}` (exclusao logica)

### Veiculos
- `GET /api/veiculos`
- `POST /api/veiculos`
- `DELETE /api/veiculos/{id}` (exclusao logica)

### Jornadas Operacionais
- `POST /api/jornadas/inicio`
- `POST /api/jornadas/fim`
- `GET /api/jornadas?inicio=2026-07-01&fim=2026-07-31`

### Recargas
- `POST /api/recargas`
- `GET /api/recargas?inicio=2026-07-01&fim=2026-07-31`
- `DELETE /api/recargas/{id}` (exclusao logica)

## Documentacao da API

Com a aplicacao em execucao, acesse o Swagger UI:

```
http://localhost:8080/api/swagger-ui.html
```

## Variaveis de ambiente

| Variavel      | Padrao                                                   | Descricao                |
| ------------- | -------------------------------------------------------- | ------------------------ |
| `DB_HOST`     | `localhost`                                              | Host do PostgreSQL       |
| `DB_PORT`     | `5432`                                                   | Porta do PostgreSQL      |
| `DB_NAME`     | `rideprofit`                                             | Nome do banco de dados   |
| `DB_USER`     | `rideprofit`                                             | Usuario do banco         |
| `DB_PASSWORD` | `rideprofit`                                             | Senha do banco           |
| `JWT_SECRET`  | `chave-temporaria-para-desenvolvimento-mude-em-producao` | Chave secreta dos tokens |
| `SERVER_PORT` | `8080`                                                   | Porta da aplicacao       |

## Deployment

### Docker Compose (Produção)

```bash
docker compose up -d
```

A aplicação estará disponível em `http://localhost:8080/api` e o health check em `http://localhost:8080/api/actuator/health`.

### Oracle Cloud (Always Free Tier)

O backend está configurado para rodar no Oracle Always Free Tier com as seguintes otimizações:

- **Health Checks**: Spring Boot Actuator configurado em `/api/actuator/health`
- **JVM Memory**: Limitado a 512MB heap (256MB initial) para se adequar aos limites do Always Free
- **Connection Pooling**: HikariCP configurado com máximo de 10 conexões
- **Resource Limits**: Docker containers com limites de CPU e memória

#### Passos para Deployment:

Veja o guia completo em `DEPLOYMENT_ORACLE.md` com instruções detalhadas para:

1. Criar Compute Instance (VM.Standard.A1.Flex preferencial)
2. Configurar Security List (apenas portas 80/443 expostas)
3. Instalar Docker e Docker Compose
4. Deploy com Nginx reverse proxy
5. Configurar SSL com Let's Encrypt
6. Configurar auto-restart com Systemd

#### Recursos Oracle Always Free Utilizados:
- **Compute**: VM.Standard.A1.Flex (2-4 OCPUs, 12-24GB RAM) ou VM.Standard.E2.1.Micro (alternativa)
- **PostgreSQL**: Rodará no mesmo compute instance via Docker
- **Storage**: Volume Docker para persistência do banco
- **Arquitetura**: Nginx (80/443) → Spring Boot (8080) → PostgreSQL (5432)
