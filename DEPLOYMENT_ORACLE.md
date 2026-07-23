# Deployment na Oracle Cloud Always Free Tier

## Pré-requisitos

- Conta Oracle Cloud (Always Free Tier)
- SSH key configurada na Oracle Cloud
- Cliente SSH instalado

## Passo 1: Criar Compute Instance (VM)

1. Acesse [Oracle Cloud Console](https://console.oraclecloud.com)
2. Navegue para **Compute** > **Instances**
3. Clique em **Create Instance**

### Basic Information

- **Name**: `rideprofit-backend`
- **Create in compartment**: Selecione seu compartment (ex: `marcosdev2002`)
- **Placement**:
  - **Availability domain**: Mantenha o padrão (AD 1)
- **Capacity type**: Mantenha `On-demand capacity`

### Image and Shape

- **Image**: Oracle Linux 9 (já selecionado)
- **Shape**: Clique em **Change shape**
- **Shape series**: Selecione **Ampere** (Arm-based processor)

  **Selecione VM.Standard.A1.Flex**:
  - **Shape name**: `VM.Standard.A1.Flex` (Always Free-eligible)
  - **OCPUs**: Configure para **4** (máximo Always Free)
  - **Memory (GB)**: Configure para **24** (máximo Always Free)
  - Clique em **Select shape**

  **Se A1 Flex não aparecer**:
  - Use `VM.Standard.E2.1.Micro` (Always Free-eligible)
  - 1 core OCPU, 1 GB memory
  - Clique em **Select shape**

⚠️ **Nota**: Para Always Free, use no máximo 4 OCPUs e 24GB RAM no VM.Standard.A1.Flex.

- **Advanced options**:
  - **Instance metadata service**: Pode deixar desabilitado ou habilitar `Require an authorization header` para maior segurança
  - **Initialization script**: Deixe em branco por enquanto
  - **Oracle Cloud Agent**: Mantenha pelo menos `Compute Instance Monitoring` habilitado

### Availability Configuration (próxima tela)

- **Live migration**: Selecione **Use live migration if possible**
  - Isso permite migração sem interrupção durante manutenção
  - Melhor para produção

### Tagging (próxima tela)

- **Tags**: Deixe vazio por enquanto

### Networking (próxima tela)

- **VCN**: Create new VCN
- **Subnet**: Public subnet
- **Assign public IP**: Yes

### SSH Keys (próxima tela)

- **SSH key type**: Choose SSH key file
- **SSH key file**: Selecione sua chave pública (`.pub`)

5. Clique em **Create**

⚠️ **Nota**: A disponibilidade do `VM.Standard.A1.Flex` depende da capacidade da região. Se aparecer "Out of host capacity", tente novamente depois ou use a opção Micro.

## Passo 2: Configurar Security List (Firewall)

1. Navegue para **Networking** > **Virtual Cloud Networks**
2. Selecione sua VCN
3. Vá para **Security Lists** > **Default Security List**
4. Adicione Ingress Rules:
   - **Port 22**: TCP (seu IP apenas) - SSH
   - **Port 80**: TCP (0.0.0.0/0) - HTTP (Nginx)
   - **Port 443**: TCP (0.0.0.0/0) - HTTPS (Nginx)

⚠️ **Importante**: Não exponha a porta 8080 publicamente. Use Nginx como reverse proxy.

## Passo 3: Conectar via SSH

No Windows, gerar chave SSH se não tiver:

```bash
ssh-keygen -t ed25519
```

Conectar à VM:

```bash
ssh -i caminho/para/id_ed25519 opc@<public-ip-da-vm>
```

⚠️ Use apenas a chave pública (`.pub`) na Oracle Cloud. Nunca compartilhe a chave privada.

## Passo 4: Instalar Docker na VM

```bash
# Atualizar sistema
sudo dnf update -y

# Instalar Docker
sudo dnf install docker -y

# Iniciar e habilitar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Adicionar usuário ao grupo docker
sudo usermod -aG docker opc

# Fazer logout e login novamente para aplicar mudanças
exit
ssh -i caminho/para/id_ed25519 opc@<public-ip-da-vm>
```

Verificar versão do Docker Compose:

```bash
docker compose version
```

## Passo 5: Clonar Repositório

```bash
# Instalar git
sudo dnf install git -y

# Clonar repositório
git clone https://github.com/seu-usuario/byd-backend.git
cd byd-backend
```

## Passo 6: Configurar Variáveis de Ambiente

```bash
# Criar arquivo .env
cp .env.example .env

# Editar .env com valores reais
nano .env
```

Configure as seguintes variáveis:

```env
DB_HOST=postgres
DB_PORT=5432
DB_NAME=rideprofit
DB_USER=rideprofit
DB_PASSWORD=<senha-forte-aqui>
JWT_SECRET=<chave-jwt-muito-forte-aqui>
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

**Importante**: Use senhas fortes e únicas!

## Passo 7: Build e Deploy com Docker Compose

Usaremos Docker Compose (já incluído no projeto):

```bash
# Build e iniciar containers
docker compose up -d --build
```

Isso iniciará:
- PostgreSQL (porta 5432 interna)
- Backend Spring Boot (porta 8080 interna)

## Passo 8: Verificar Deployment

```bash
# Verificar containers
docker compose ps

# Verificar logs do backend
docker compose logs -f app

# Verificar health check
curl http://localhost:8080/api/actuator/health
```

## Passo 9: Configurar Nginx Reverse Proxy (Obrigatório para produção)

```bash
# Instalar Nginx
sudo dnf install nginx -y

# Configurar Nginx
sudo nano /etc/nginx/conf.d/rideprofit.conf
```

Adicione:

```nginx
server {
    listen 80;
    server_name <seu-dominio-ou-ip>;

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /actuator/ {
        proxy_pass http://localhost:8080/actuator/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Iniciar Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Testar
curl http://<public-ip>/api/actuator/health
```

## Passo 10: Configurar SSL com Let's Encrypt (Recomendado para produção)

```bash
# Instalar Certbot
sudo dnf install certbot python3-certbot-nginx -y

# Obter certificado
sudo certbot --nginx -d <seu-dominio>

# Configurar renovação automática
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer
```

## Passo 11: Configurar Auto-restart com Systemd

```bash
# Criar systemd service para Docker Compose
sudo nano /etc/systemd/system/rideprofit.service
```

Adicione:

```ini
[Unit]
Description=Ride Profit Backend
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/opc/byd-backend
ExecStart=/usr/bin/docker compose up -d
ExecStop=/usr/bin/docker compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

⚠️ **Nota**: Verifique se o comando é `docker compose` ou `docker-compose`:

```bash
docker compose version
# ou
docker-compose version
```

Ajuste o `ExecStart` e `ExecStop` conforme necessário.

```bash
# Habilitar service
sudo systemctl enable rideprofit.service
sudo systemctl start rideprofit.service
```

## Arquitetura Final

```
┌─────────────────────────────┐
│        Internet             │
└──────────────┬──────────────┘
               │
               ▼
        ┌─────────────┐
        │    Nginx    │
        │   80 / 443  │
        └──────┬──────┘
               │
               ▼
        ┌─────────────┐
        │ Spring Boot │
        │   :8080     │
        └──────┬──────┘
               │
               ▼
        ┌─────────────┐
        │ PostgreSQL  │
        │   :5432     │
        └─────────────┘
```

Todos os serviços rodam em containers Docker na mesma VM.

## Monitoramento e Manutenção

### Verificar logs:
```bash
docker compose logs -f app
docker compose logs -f postgres
```

### Reiniciar containers:
```bash
docker compose restart
```

### Atualizar aplicação:
```bash
cd ~/byd-backend
git pull
docker compose up -d --build
```

### Backup do banco:
```bash
docker compose exec postgres pg_dump -U rideprofit rideprofit > backup.sql
```

### Restaurar backup:
```bash
docker compose exec -T postgres psql -U rideprofit rideprofit < backup.sql
```

## Endpoints Importantes

- **API**: `http://<public-ip>/api`
- **Health Check**: `http://<public-ip>/api/actuator/health`
- **Swagger UI**: `http://<public-ip>/api/swagger-ui.html`

## Solução de Problemas

### Container não inicia:
```bash
docker compose logs app
```

### PostgreSQL não conecta:
```bash
docker compose logs postgres
docker compose exec postgres psql -U rideprofit -d rideprofit
```

### Sem memória suficiente:
```bash
# Verificar uso de memória
free -h
docker compose stats
```

### Porta já em uso:
```bash
sudo lsof -i :80
sudo lsof -i :443
```

### Nginx não funciona:
```bash
sudo nginx -t
sudo systemctl status nginx
```

## Recursos Oracle Always Free Utilizados

### VM.Standard.A1.Flex (Preferencial)
- **Compute**: 2-4 OCPUs
- **RAM**: 12-24 GB
- **Storage**: 2 blocos de 50GB (usar 1 para Docker volumes)
- **Bandwidth**: 10TB/mês

### VM.Standard.E2.1.Micro (Alternativa)
- **Compute**: 1 OCPU
- **RAM**: 1 GB (compartilhada)
- **Storage**: 2 blocos de 50GB
- **Bandwidth**: 10TB/mês

## Segurança Adicional

1. **Firewall**: Use apenas portas necessárias
2. **SSH**: Desabilite login por senha, use apenas chaves
3. **Atualizações**: Mantenha sistema atualizado
4. **Backups**: Configure backups automáticos do PostgreSQL
5. **Monitoramento**: Use Oracle Cloud Monitoring gratuito

## Suporte

Em caso de problemas, verifique:
1. Logs dos containers
2. Security list da VCN
3. Configuração de variáveis de ambiente
4. Recursos disponíveis (RAM/CPU)
5. Configuração do Nginx

## Resumo das Mudanças vs Documentação Original

1. **VM**: Preferência por VM.Standard.A1.Flex (mais recursos) em vez de Micro
2. **Segurança**: Porta 8080 não exposta publicamente - usa Nginx reverse proxy
3. **Deploy**: Padronização em Docker Compose (não mistura com docker run)
4. **Systemd**: Corrigido para usar comando correto (docker compose vs docker-compose)
5. **Arquitetura**: Nginx → Spring Boot → PostgreSQL (todos em Docker)
