CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ---------------------------------------------------------------------------
-- Tenant e autenticação
-- ---------------------------------------------------------------------------

CREATE TABLE tenant (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome                VARCHAR(200) NOT NULL,
    tarifa_energia_kwh  NUMERIC(10, 4) NOT NULL DEFAULT 0.9500,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuario (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nome            VARCHAR(200) NOT NULL,
    email           VARCHAR(200) NOT NULL,
    senha           VARCHAR(500) NOT NULL,
    perfil          VARCHAR(50) NOT NULL,
    ultimo_acesso   TIMESTAMP,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      UUID,
    atualizado_por  UUID,
    CONSTRAINT uk_usuario_email UNIQUE (email)
);

CREATE TABLE veiculo (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id               UUID NOT NULL REFERENCES tenant(id),
    marca                   VARCHAR(100) NOT NULL,
    modelo                  VARCHAR(100) NOT NULL,
    ano                     INTEGER NOT NULL,
    tipo                    VARCHAR(50) NOT NULL,
    capacidade_bateria_kwh  NUMERIC(10, 3) NOT NULL,
    autonomia_km            NUMERIC(10, 2) NOT NULL,
    consumo_medio_kwh_km    NUMERIC(10, 4) NOT NULL,
    ativo                   BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por              UUID,
    atualizado_por          UUID
);

-- ---------------------------------------------------------------------------
-- Cadastros auxiliares
-- ---------------------------------------------------------------------------

CREATE TABLE categoria_despesa (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nome            VARCHAR(100) NOT NULL,
    descricao       VARCHAR(500),
    padrao          BOOLEAN NOT NULL DEFAULT FALSE,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      UUID,
    atualizado_por  UUID
);

CREATE TABLE origem (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nome            VARCHAR(100) NOT NULL,
    descricao       VARCHAR(500),
    imagem_base64   TEXT,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      UUID,
    atualizado_por  UUID,
    CONSTRAINT uk_origem_tenant_nome UNIQUE (tenant_id, nome)
);

CREATE TABLE categoria_saida (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nome            VARCHAR(100) NOT NULL,
    descricao       VARCHAR(500),
    tipo            VARCHAR(30) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      UUID,
    atualizado_por  UUID,
    CONSTRAINT uk_categoria_saida_tenant_nome UNIQUE (tenant_id, nome)
);

CREATE TABLE forma_pagamento (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nome            VARCHAR(100) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por      UUID,
    atualizado_por  UUID,
    CONSTRAINT uk_forma_pagamento_tenant_nome UNIQUE (tenant_id, nome)
);

-- ---------------------------------------------------------------------------
-- Jornada operacional
-- ---------------------------------------------------------------------------

CREATE TABLE jornada_operacional (
    id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id                   UUID NOT NULL REFERENCES tenant(id),
    usuario_id                  UUID NOT NULL REFERENCES usuario(id),
    veiculo_id                  UUID NOT NULL REFERENCES veiculo(id),
    data                        DATE NOT NULL,
    horario_inicio              TIME NOT NULL,
    horario_fim                 TIME,
    odometro_inicial            NUMERIC(10, 2) NOT NULL,
    odometro_final              NUMERIC(10, 2),
    percentual_bateria_inicial  NUMERIC(5, 2) NOT NULL,
    percentual_bateria_final    NUMERIC(5, 2),
    status                      VARCHAR(30) NOT NULL,
    ativo                       BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por                  UUID,
    atualizado_por              UUID
);

-- ---------------------------------------------------------------------------
-- Transações financeiras
-- ---------------------------------------------------------------------------

CREATE TABLE despesa (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    usuario_id           UUID NOT NULL REFERENCES usuario(id),
    jornada_id           UUID,
    categoria_despesa_id UUID REFERENCES categoria_despesa(id),
    categoria_saida_id   UUID NOT NULL REFERENCES categoria_saida(id),
    forma_pagamento_id   UUID REFERENCES forma_pagamento(id),
    valor                NUMERIC(12, 2) NOT NULL,
    moeda                VARCHAR(3) NOT NULL DEFAULT 'BRL',
    data                 DATE NOT NULL,
    dia_semana           VARCHAR(20) NOT NULL,
    tipo_gasto           VARCHAR(30) NOT NULL,
    item_manutencao      VARCHAR(500),
    nota_fiscal          BYTEA,
    nota_fiscal_nome     VARCHAR(255),
    nota_fiscal_tipo     VARCHAR(100),
    descricao            VARCHAR(500),
    observacao           VARCHAR(1000),
    ativo                BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por           UUID,
    atualizado_por       UUID
);

CREATE TABLE receita (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    usuario_id          UUID NOT NULL REFERENCES usuario(id),
    jornada_id          UUID,
    origem_id           UUID NOT NULL REFERENCES origem(id),
    valor               NUMERIC(12, 2) NOT NULL,
    moeda               VARCHAR(3) NOT NULL DEFAULT 'BRL',
    data                DATE NOT NULL,
    horario             TIME NOT NULL,
    data_hora_inicio    TIMESTAMP NOT NULL,
    data_hora_fim       TIMESTAMP NOT NULL,
    dia_semana          VARCHAR(20) NOT NULL,
    quantidade_viagens  INTEGER NOT NULL,
    quilometros_rodados NUMERIC(10, 1) NOT NULL,
    horas_trabalhadas   NUMERIC(10, 2) NOT NULL,
    plataforma          VARCHAR(50),
    observacao          VARCHAR(1000),
    nota_fiscal         BYTEA,
    nota_fiscal_nome    VARCHAR(255),
    nota_fiscal_tipo    VARCHAR(100),
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por          UUID,
    atualizado_por      UUID
);

CREATE TABLE recarga (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    usuario_id          UUID NOT NULL REFERENCES usuario(id),
    veiculo_id          UUID NOT NULL REFERENCES veiculo(id),
    jornada_id          UUID,
    despesa_id          UUID REFERENCES despesa(id),
    data                DATE NOT NULL,
    horario             TIME NOT NULL,
    percentual_inicial  NUMERIC(5, 2) NOT NULL,
    percentual_final    NUMERIC(5, 2) NOT NULL,
    kwh_consumidos      NUMERIC(10, 3) NOT NULL,
    tarifa_kwh          NUMERIC(10, 4) NOT NULL,
    custo               NUMERIC(12, 2) NOT NULL,
    moeda_custo         VARCHAR(3) NOT NULL DEFAULT 'BRL',
    local_recarga       VARCHAR(200),
    observacao          VARCHAR(1000),
    nota_fiscal         BYTEA,
    nota_fiscal_nome    VARCHAR(255),
    nota_fiscal_tipo    VARCHAR(100),
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por          UUID,
    atualizado_por      UUID
);

-- ---------------------------------------------------------------------------
-- Índices
-- ---------------------------------------------------------------------------

CREATE INDEX idx_usuario_tenant ON usuario(tenant_id);
CREATE INDEX idx_usuario_email ON usuario(email);

CREATE INDEX idx_veiculo_tenant ON veiculo(tenant_id);

CREATE INDEX idx_categoria_despesa_tenant ON categoria_despesa(tenant_id);

CREATE INDEX idx_origem_tenant ON origem(tenant_id);
CREATE INDEX idx_categoria_saida_tenant ON categoria_saida(tenant_id);
CREATE INDEX idx_forma_pagamento_tenant ON forma_pagamento(tenant_id);

CREATE INDEX idx_jornada_tenant_data ON jornada_operacional(tenant_id, data);
CREATE INDEX idx_jornada_usuario ON jornada_operacional(usuario_id);
CREATE INDEX idx_jornada_veiculo ON jornada_operacional(veiculo_id);

CREATE INDEX idx_despesa_tenant_data ON despesa(tenant_id, data);
CREATE INDEX idx_despesa_usuario ON despesa(usuario_id);
CREATE INDEX idx_despesa_categoria ON despesa(categoria_despesa_id);
CREATE INDEX idx_despesa_categoria_saida ON despesa(categoria_saida_id);
CREATE INDEX idx_despesa_forma_pagamento ON despesa(forma_pagamento_id);

CREATE INDEX idx_receita_tenant_data ON receita(tenant_id, data);
CREATE INDEX idx_receita_usuario ON receita(usuario_id);
CREATE INDEX idx_receita_jornada ON receita(jornada_id);
CREATE INDEX idx_receita_origem ON receita(origem_id);

CREATE INDEX idx_recarga_tenant_data ON recarga(tenant_id, data);
CREATE INDEX idx_recarga_usuario ON recarga(usuario_id);
CREATE INDEX idx_recarga_veiculo ON recarga(veiculo_id);
