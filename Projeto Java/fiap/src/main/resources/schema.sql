
-- -----------------------------------------------------------------------------
-- GRUPO 1: Configuração, Acesso e White Label
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Hoteis (
    id                   INT           NOT NULL AUTO_INCREMENT,
    nome_estabelecimento VARCHAR(150)  NOT NULL,
    cnpj                 VARCHAR(18)   NOT NULL,
    status_licenca       VARCHAR(20)   NOT NULL,   -- 'ATIVO' | 'INATIVO'
    email_suporte_mock   VARCHAR(100),
    CONSTRAINT pk_hoteis PRIMARY KEY (id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Usuarios_Portal (
    id        INT          NOT NULL AUTO_INCREMENT,
    hotel_id  INT          NOT NULL,
    username  VARCHAR(50)  NOT NULL,
    password     VARCHAR(100) NOT NULL,
    cargo     VARCHAR(30)  NOT NULL,               -- 'GERENTE' | 'RECEPCAO'
    CONSTRAINT pk_usuarios_portal PRIMARY KEY (id),
    CONSTRAINT fk_usuarios_hotel  FOREIGN KEY (hotel_id) REFERENCES Hoteis(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Idiomas (
    id          INT          NOT NULL AUTO_INCREMENT,
    hotel_id    INT          NOT NULL,
    codigo_iso  VARCHAR(5)   NOT NULL,
    nome_idioma VARCHAR(50)  NOT NULL,
    ativo       BOOLEAN      NOT NULL,
    CONSTRAINT pk_idiomas       PRIMARY KEY (id),
    CONSTRAINT fk_idiomas_hotel FOREIGN KEY (hotel_id) REFERENCES Hoteis(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Dicionario_Totem (
    id                INT          NOT NULL AUTO_INCREMENT,
    hotel_id          INT          NOT NULL,
    idioma_id         INT          NOT NULL,
    chave_componente  VARCHAR(100) NOT NULL,
    texto_traduzido   VARCHAR(500) NOT NULL,
    CONSTRAINT pk_dicionario_totem  PRIMARY KEY (id),
    CONSTRAINT fk_dicionario_hotel  FOREIGN KEY (hotel_id)  REFERENCES Hoteis(id),
    CONSTRAINT fk_dicionario_idioma FOREIGN KEY (idioma_id) REFERENCES Idiomas(id)
);

-- -----------------------------------------------------------------------------
-- ATENÇÃO — Arquitetura de Mídias (RN Projeto):
-- Imagens NÃO são armazenadas como BLOB no banco H2.
-- Os campos de imagem abaixo guardam apenas o caminho de texto (VARCHAR)
-- referente ao arquivo físico salvo em /flexmedia_uploads/.
-- O Spring Boot serve essa pasta via ambiente web e o Thymeleaf injeta a
-- tag <img src="/uploads/..."> em tempo de execução.
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Config_White_Label (
    id                    INT          NOT NULL AUTO_INCREMENT,
    hotel_id              INT          NOT NULL,
    cor_primaria_hex      VARCHAR(7)   NOT NULL,
    cor_secundaria_hex    VARCHAR(7)   NOT NULL,
    logo_path             VARCHAR(255),            -- Caminho texto: uploads/logos/hotel1_logo.png
    splash_image_path     VARCHAR(255),            -- Caminho texto: uploads/splash/hotel1_splash.jpg
    info_hotel_texto      TEXT,
    info_hotel_image_path VARCHAR(255),            -- Caminho texto: uploads/info/hotel1_info.jpg
    CONSTRAINT pk_config_white_label       PRIMARY KEY (id),
    CONSTRAINT fk_config_white_label_hotel FOREIGN KEY (hotel_id) REFERENCES Hoteis(id)
);

-- -----------------------------------------------------------------------------
-- GRUPO 2: Operações e Infraestrutura Física
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Quartos (
    id             INT          NOT NULL AUTO_INCREMENT,
    hotel_id       INT          NOT NULL,
    numero_quarto  VARCHAR(10)  NOT NULL,
    tipo_quarto    VARCHAR(50)  NOT NULL,    -- v2: Ex: 'Quarto Casal', 'Suite Master'
    status         VARCHAR(20)  NOT NULL,    -- 'LIVRE' | 'OCUPADO' | 'LIMPEZA'
    CONSTRAINT pk_quartos       PRIMARY KEY (id),
    CONSTRAINT fk_quartos_hotel FOREIGN KEY (hotel_id) REFERENCES Hoteis(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Produtos_Frigobar (
    id             INT            NOT NULL AUTO_INCREMENT,
    hotel_id       INT            NOT NULL,
    nome_produto   VARCHAR(100)   NOT NULL,
    preco_unitario DECIMAL(10,2)  NOT NULL,
    CONSTRAINT pk_produtos_frigobar       PRIMARY KEY (id),
    CONSTRAINT fk_produtos_frigobar_hotel FOREIGN KEY (hotel_id) REFERENCES Hoteis(id)
);

-- -----------------------------------------------------------------------------
-- GRUPO 3: Jornada do Cliente e Simulações
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Reservas_Hospedes (
    id                         INT          NOT NULL AUTO_INCREMENT,
    hotel_id                   INT          NOT NULL,
    codigo_reserva             VARCHAR(20)  NOT NULL,
    nome_hospede               VARCHAR(150) NOT NULL,
    documento_cpf_passaporte   VARCHAR(30)  NOT NULL,
    quantidade_pessoas         INT          NOT NULL,
    data_entrada_prevista      TIMESTAMP     NOT NULL,
    data_saida_prevista        TIMESTAMP     NOT NULL,
    data_entrada_real          TIMESTAMP,
    data_saida_real            TIMESTAMP,
    termo_consentimento_aceito BOOLEAN      NOT NULL,
    quarto_id                  INT,                   -- Pode ser NULL antes do check-in
    status_reserva             VARCHAR(25)  NOT NULL, -- 'PENDENTE' | 'RESERVA_ATIVA' | 'FINALIZADA'
    CONSTRAINT pk_reservas_hospedes PRIMARY KEY (id),
    CONSTRAINT fk_reservas_hotel    FOREIGN KEY (hotel_id)  REFERENCES Hoteis(id),
    CONSTRAINT fk_reservas_quarto   FOREIGN KEY (quarto_id) REFERENCES Quartos(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Cartoes_Chave (
    id                  INT          NOT NULL AUTO_INCREMENT,
    hotel_id            INT          NOT NULL,
    reserva_id          INT          NOT NULL,
    numero_cartao_chave VARCHAR(50)  NOT NULL,
    status_cartao       VARCHAR(20)  NOT NULL,   -- 'ATIVO' | 'INATIVO'
    CONSTRAINT pk_cartoes_chave   PRIMARY KEY (id),
    CONSTRAINT fk_cartoes_hotel   FOREIGN KEY (hotel_id)   REFERENCES Hoteis(id),
    CONSTRAINT fk_cartoes_reserva FOREIGN KEY (reserva_id) REFERENCES Reservas_Hospedes(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Consumos_Estadia (
    id                     INT            NOT NULL AUTO_INCREMENT,
    hotel_id               INT            NOT NULL,
    reserva_id             INT            NOT NULL,
    tipo_consumo           VARCHAR(30)    NOT NULL,   -- 'DIARIA' | 'FRIGOBAR' | 'LAVANDERIA' | 'ROOM_SERVICE'
    descricao_item         VARCHAR(150)   NOT NULL,
    quantidade             INT            NOT NULL,
    preco_unitario_momento DECIMAL(10,2)  NOT NULL,
    valor_total_item       DECIMAL(10,2)  NOT NULL,   -- quantidade * preco_unitario_momento (imutavel)
    CONSTRAINT pk_consumos_estadia  PRIMARY KEY (id),
    CONSTRAINT fk_consumos_hotel    FOREIGN KEY (hotel_id)   REFERENCES Hoteis(id),
    CONSTRAINT fk_consumos_reserva  FOREIGN KEY (reserva_id) REFERENCES Reservas_Hospedes(id)
);

-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Transacoes_Pagamento (
    id                  INT            NOT NULL AUTO_INCREMENT,
    hotel_id            INT            NOT NULL,
    reserva_id          INT            NOT NULL,
    metodo_pagamento    VARCHAR(30)    NOT NULL,   -- 'PIX' | 'CARTAO_CREDITO' | 'CARTAO_DEBITO'
    valor_pago          DECIMAL(10,2)  NOT NULL,
    data_hora_pagamento TIMESTAMP       NOT NULL,
    CONSTRAINT pk_transacoes_pagamento PRIMARY KEY (id),
    CONSTRAINT fk_transacoes_hotel     FOREIGN KEY (hotel_id)   REFERENCES Hoteis(id),
    CONSTRAINT fk_transacoes_reserva   FOREIGN KEY (reserva_id) REFERENCES Reservas_Hospedes(id)
);
