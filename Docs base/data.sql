-- =============================================================================
-- FlexMedia Hotel Hub
-- data.sql — Massa de Dados Mockada (DML) — v2
-- Local: src/main/resources/data.sql
-- O Spring Boot executa este script após o schema.sql na inicialização.
--
-- Ordem dos INSERTs (respeita hierarquia de FKs):
--   Hoteis
--   → Config_White_Label, Idiomas, Usuarios_Portal
--   → Quartos, Produtos_Frigobar
--   → Reservas_Hospedes
--   → Cartoes_Chave, Consumos_Estadia, Transacoes_Pagamento
--   → Dicionario_Totem  (depende de Hoteis + Idiomas)
-- =============================================================================

-- =============================================================================
-- [1/11] HOTEIS
-- RN01: Dois hotéis distintos para demonstrar isolamento multi-tenant.
-- =============================================================================

INSERT INTO Hoteis (nome_estabelecimento, cnpj, status_licenca, email_suporte_mock) VALUES
    ('Grand Palace Hotel',  '12.345.678/0001-90', 'ATIVO', 'suporte@grandpalace.com.br'),
    ('Pousada Vista Verde',  '98.765.432/0001-11', 'ATIVO', 'contato@vistaverde.com.br');
-- IDs: 1 = Grand Palace | 2 = Pousada Vista Verde

-- =============================================================================
-- [2/11] CONFIG_WHITE_LABEL
-- RN02: Caminhos de texto — sem BLOB. Relacao 1:1 com Hoteis.
-- =============================================================================

INSERT INTO Config_White_Label
    (hotel_id, cor_primaria_hex, cor_secundaria_hex,
     logo_path, splash_image_path,
     info_hotel_texto, info_hotel_image_path)
VALUES
    (1, '#1a56db', '#1e293b',
     'uploads/logos/hotel1_logo.png',
     'uploads/splash/hotel1_splash.jpg',
     'O Grand Palace oferece experiencia de alto padrao no coracao da cidade, com quartos sofisticados e atendimento 24h.',
     'uploads/info/hotel1_info.jpg'),

    (2, '#16a34a', '#14532d',
     'uploads/logos/hotel2_logo.png',
     'uploads/splash/hotel2_splash.jpg',
     'A Pousada Vista Verde e um refugio tranquilo cercado de natureza, perfeito para descanso e lazer em familia.',
     'uploads/info/hotel2_info.jpg');

-- =============================================================================
-- [3/11] IDIOMAS
-- IDs gerados: 1=pt(H1) | 2=en(H1) | 3=es(H1) | 4=pt(H2) | 5=en(H2)
-- =============================================================================

INSERT INTO Idiomas (hotel_id, codigo_iso, nome_idioma, ativo) VALUES
    (1, 'pt', 'Portugues', TRUE),
    (1, 'en', 'English',   TRUE),
    (1, 'es', 'Espanol',   FALSE),
    (2, 'pt', 'Portugues', TRUE),
    (2, 'en', 'English',   TRUE);

-- =============================================================================
-- [4/11] USUARIOS_PORTAL
-- =============================================================================

INSERT INTO Usuarios_Portal (hotel_id, username, password, cargo) VALUES
    (1, 'gerente_palace',  '$2a$10$FakeHashGrandPalaceGerente',  'GERENTE'),
    (1, 'recepcao_palace', '$2a$10$FakeHashGrandPalaceRecepcao', 'RECEPCAO'),
    (2, 'gerente_verde',   '$2a$10$FakeHashVistaVerdeGerente',   'GERENTE'),
    (2, 'recepcao_verde',  '$2a$10$FakeHashVistaVerdeRecepcao',  'RECEPCAO');

-- =============================================================================
-- [5/11] QUARTOS
-- RN03: Os tres estados devem estar representados.
-- v2: Coluna tipo_quarto adicionada com valores coerentes por unidade.
-- IDs: 1=101(H1) | 2=102(H1) | 3=Suite201(H1) | 4=103(H1)
--      5=Chale01(H2) | 6=Chale02(H2) | 7=Chale03(H2)
-- =============================================================================

INSERT INTO Quartos (hotel_id, numero_quarto, tipo_quarto, status) VALUES
    (1, '101',       'Quarto Standard',   'LIVRE'),
    (1, '102',       'Quarto Casal',      'OCUPADO'),
    (1, 'Suite 201', 'Suite Master',      'LIMPEZA'),  -- Checkout recente; aguarda liberacao manual
    (1, '103',       'Quarto Triplo',     'LIVRE'),
    (2, 'Chale 01',  'Chale Romantico',   'LIVRE'),
    (2, 'Chale 02',  'Chale Familia',     'OCUPADO'),
    (2, 'Chale 03',  'Chale Premium',     'LIMPEZA');  -- Checkout recente; aguarda liberacao manual

-- =============================================================================
-- [6/11] PRODUTOS_FRIGOBAR
-- =============================================================================

INSERT INTO Produtos_Frigobar (hotel_id, nome_produto, preco_unitario) VALUES
    (1, 'Agua Mineral 500ml',  8.00),
    (1, 'Refrigerante Lata',  12.00),
    (1, 'Snack Batata',       15.00),
    (1, 'Vinho Tinto 375ml',  75.00),
    (2, 'Agua Mineral 500ml',  7.00),
    (2, 'Suco de Laranja',    14.00),
    (2, 'Chocolate Barra',    18.00);

-- =============================================================================
-- [7/11] RESERVAS_HOSPEDES
-- IDs: 1=GP-001(ativa) | 2=GP-002(finalizada) | 3=GP-003(pendente)
--      4=VV-001(ativa)  | 5=VV-002(finalizada)
-- =============================================================================

INSERT INTO Reservas_Hospedes
    (hotel_id, codigo_reserva, nome_hospede, documento_cpf_passaporte,
     quantidade_pessoas, data_entrada_prevista, data_saida_prevista,
     data_entrada_real, data_saida_real,
     termo_consentimento_aceito, quarto_id, status_reserva)
VALUES
    -- H1: Reserva ativa — hospede no quarto 102 (OCUPADO)
    (1, 'GP-2025-001', 'Ana Beatriz Souza',       '345.678.901-23',
     2, '2025-07-10 14:00:00', '2025-07-15 12:00:00',
     '2025-07-10 14:32:00', NULL,
     TRUE, 2, 'RESERVA_ATIVA'),

    -- H1: Reserva finalizada — quarto Suite 201 agora em LIMPEZA
    (1, 'GP-2025-002', 'Carlos Henrique Lima',    '456.789.012-34',
     1, '2025-07-05 14:00:00', '2025-07-10 12:00:00',
     '2025-07-05 15:10:00', '2025-07-10 11:45:00',
     TRUE, 3, 'FINALIZADA'),

    -- H1: Reserva pendente — sem quarto atribuido ainda
    (1, 'GP-2025-003', 'Mariana Ferreira Costa',  '567.890.123-45',
     3, '2025-07-20 14:00:00', '2025-07-25 12:00:00',
     NULL, NULL,
     FALSE, NULL, 'PENDENTE'),

    -- H2: Reserva ativa — hospede no Chale 02 (OCUPADO)
    (2, 'VV-2025-001', 'Roberto Alves Nunes',     'AB123456',
     2, '2025-07-08 14:00:00', '2025-07-14 12:00:00',
     '2025-07-08 13:55:00', NULL,
     TRUE, 6, 'RESERVA_ATIVA'),

    -- H2: Reserva finalizada — Chale 03 agora em LIMPEZA
    (2, 'VV-2025-002', 'Fernanda Oliveira Matos', '678.901.234-56',
     1, '2025-07-01 14:00:00', '2025-07-08 12:00:00',
     '2025-07-01 14:20:00', '2025-07-08 11:30:00',
     TRUE, 7, 'FINALIZADA');

-- =============================================================================
-- [8/11] CARTOES_CHAVE
-- RN04: ATIVO para reservas em andamento; INATIVO para finalizadas.
-- =============================================================================

INSERT INTO Cartoes_Chave (hotel_id, reserva_id, numero_cartao_chave, status_cartao) VALUES
    (1, 1, 'KEY-A1B2C3D4E5F6', 'ATIVO'),    -- GP-001 em andamento
    (1, 2, 'KEY-G7H8I9J0K1L2', 'INATIVO'),  -- GP-002 finalizada; desativado no checkout
    (2, 4, 'KEY-M3N4O5P6Q7R8', 'ATIVO'),    -- VV-001 em andamento
    (2, 5, 'KEY-S9T0U1V2W3X4', 'INATIVO');  -- VV-002 finalizada; desativado no checkout

-- =============================================================================
-- [9/11] CONSUMOS_ESTADIA
-- =============================================================================

INSERT INTO Consumos_Estadia
    (hotel_id, reserva_id, tipo_consumo, descricao_item, quantidade,
     preco_unitario_momento, valor_total_item)
VALUES
    (1, 1, 'DIARIA',       '5 Diarias - Quarto 102',    5, 320.00, 1600.00),
    (1, 1, 'FRIGOBAR',     'Agua Mineral 500ml',         3,   8.00,   24.00),
    (1, 1, 'FRIGOBAR',     'Vinho Tinto 375ml',          1,  75.00,   75.00),
    (1, 1, 'ROOM_SERVICE', 'Cafe da Manha no Quarto',    2,  45.00,   90.00),
    (1, 2, 'DIARIA',       '5 Diarias - Suite 201',      5, 480.00, 2400.00),
    (1, 2, 'LAVANDERIA',   'Lavagem de Terno',           1,  60.00,   60.00),
    (1, 2, 'FRIGOBAR',     'Refrigerante Lata',          4,  12.00,   48.00),
    (2, 4, 'DIARIA',       '6 Diarias - Chale 02',       6, 250.00, 1500.00),
    (2, 4, 'FRIGOBAR',     'Suco de Laranja',            2,  14.00,   28.00),
    (2, 5, 'DIARIA',       '7 Diarias - Chale 03',       7, 250.00, 1750.00),
    (2, 5, 'FRIGOBAR',     'Chocolate Barra',            3,  18.00,   54.00);

-- =============================================================================
-- [10/11] TRANSACOES_PAGAMENTO
-- Apenas reservas FINALIZADAS possuem transacao de pagamento.
-- =============================================================================

INSERT INTO Transacoes_Pagamento
    (hotel_id, reserva_id, metodo_pagamento, valor_pago, data_hora_pagamento)
VALUES
    (1, 2, 'PIX',            2508.00, '2025-07-10 11:45:00'),
    (2, 5, 'CARTAO_CREDITO', 1804.00, '2025-07-08 11:30:00');

-- =============================================================================
-- [11/11] DICIONARIO_TOTEM — v2 (mapeamento completo de telas)
--
-- Estrutura dos blocos:
--   A. Navegacao Geral
--   B. Tela de Identificacao
--   C. Mensagens de Status e Loading
--   D. Tela de Resumo do Check-in
--   E. Tela de Sucesso do Check-in
--   F. Tela de Resumo do Checkout e Consumos
--   G. Tela de Pagamento e Maquininha
--   H. Tela de Checkout Concluido (Sucesso)
--
-- hotel_id=1, idioma_id=1 → Grand Palace / Portugues
-- hotel_id=1, idioma_id=2 → Grand Palace / English
-- hotel_id=2, idioma_id=4 → Pousada Vista Verde / Portugues
-- =============================================================================

INSERT INTO Dicionario_Totem (hotel_id, idioma_id, chave_componente, texto_traduzido) VALUES

-- ============================================================
-- A. NAVEGACAO GERAL
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'btn_iniciar',           'Iniciar'),
(1, 1, 'btn_continuar',         'Continuar'),
(1, 1, 'btn_voltar',            'Voltar'),
(1, 1, 'btn_realizar_checkin',  'Realizar Check-in'),
(1, 1, 'btn_realizar_checkout', 'Realizar Check-out'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'btn_iniciar',           'Start'),
(1, 2, 'btn_continuar',         'Continue'),
(1, 2, 'btn_voltar',            'Go Back'),
(1, 2, 'btn_realizar_checkin',  'Perform Check-in'),
(1, 2, 'btn_realizar_checkout', 'Perform Check-out'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'btn_iniciar',           'Iniciar'),
(2, 4, 'btn_continuar',         'Continuar'),
(2, 4, 'btn_voltar',            'Voltar'),
(2, 4, 'btn_realizar_checkin',  'Realizar Check-in'),
(2, 4, 'btn_realizar_checkout', 'Realizar Check-out'),

-- ============================================================
-- B. TELA DE IDENTIFICACAO
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'label_primeiro_nome',         'Primeiro Nome'),
(1, 1, 'label_documento',             'CPF ou Passaporte'),
(1, 1, 'label_codigo_reserva',        'Codigo da Reserva'),
(1, 1, 'placeholder_nome',            'Ex. Joao / Maria'),
(1, 1, 'placeholder_documento',       'Ex. 485.584.123-00'),
(1, 1, 'placeholder_codigo_reserva',  'Ex. ABC123DFG456'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'label_primeiro_nome',         'First Name'),
(1, 2, 'label_documento',             'ID or Passport'),
(1, 2, 'label_codigo_reserva',        'Booking Code'),
(1, 2, 'placeholder_nome',            'E.g. John / Mary'),
(1, 2, 'placeholder_documento',       'E.g. AB1234567'),
(1, 2, 'placeholder_codigo_reserva',  'E.g. ABC123DFG456'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'label_primeiro_nome',         'Primeiro Nome'),
(2, 4, 'label_documento',             'CPF ou Passaporte'),
(2, 4, 'label_codigo_reserva',        'Codigo da Reserva'),
(2, 4, 'placeholder_nome',            'Ex. Joao / Maria'),
(2, 4, 'placeholder_documento',       'Ex. 485.584.123-00'),
(2, 4, 'placeholder_codigo_reserva',  'Ex. ABC123DFG456'),

-- ============================================================
-- C. MENSAGENS DE STATUS E LOADING
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_titulo_aguarde',              'Aguarde...'),
(1, 1, 'txt_desc_velocidade_luz',         'Estamos buscando suas informacoes na velocidade da luz...'),
(1, 1, 'txt_titulo_codificando_cartao',   'Codificando seu Cartao-Chave...'),
(1, 1, 'txt_desc_configurando_cartao',    'Estamos configurando seu cartao para uma estadia impecavel.'),
(1, 1, 'txt_desc_conectando_servidor',    'Conectando com o servidor...'),
(1, 1, 'txt_desc_aguardando_pagamento',   'Aguardando pagamento...'),
(1, 1, 'txt_desc_redirecionando',         'Redirecionando para o inicio...'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_titulo_aguarde',              'Please wait...'),
(1, 2, 'txt_desc_velocidade_luz',         'We are retrieving your information at the speed of light...'),
(1, 2, 'txt_titulo_codificando_cartao',   'Encoding your Key Card...'),
(1, 2, 'txt_desc_configurando_cartao',    'We are setting up your card for a flawless stay.'),
(1, 2, 'txt_desc_conectando_servidor',    'Connecting to the server...'),
(1, 2, 'txt_desc_aguardando_pagamento',   'Awaiting payment...'),
(1, 2, 'txt_desc_redirecionando',         'Redirecting to the start screen...'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_titulo_aguarde',              'Aguarde...'),
(2, 4, 'txt_desc_velocidade_luz',         'Estamos buscando suas informacoes na velocidade da luz...'),
(2, 4, 'txt_titulo_codificando_cartao',   'Codificando seu Cartao-Chave...'),
(2, 4, 'txt_desc_configurando_cartao',    'Estamos configurando seu cartao para uma estadia impecavel.'),
(2, 4, 'txt_desc_conectando_servidor',    'Conectando com o servidor...'),
(2, 4, 'txt_desc_aguardando_pagamento',   'Aguardando pagamento...'),
(2, 4, 'txt_desc_redirecionando',         'Redirecionando para o inicio...'),

-- ============================================================
-- D. TELA DE RESUMO DO CHECK-IN (pos-busca)
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_prazer_receber',       'e um prazer recebermos voce em nosso espaco!'),
(1, 1, 'txt_verifique_detalhes',   'Verifique abaixo os detalhes da sua reserva para garantirmos que esta tudo do seu jeito:'),
(1, 1, 'label_acomodacao',         'Acomodacao'),
(1, 1, 'label_qtd_pessoas',        'Numero de Hospedes'),
(1, 1, 'label_checkin',            'Data de Check-in'),
(1, 1, 'label_checkout',           'Data de Check-out'),
(1, 1, 'txt_bloco_termo_seguranca','Mas ainda nao acabou... Para sua seguranca e de todos os hospedes, solicitamos que leia e aceite nosso Termo de Consentimento e Regulamento Interno antes de prosseguir. Suas informacoes sao tratadas com total sigilo, conforme a LGPD.'),
(1, 1, 'txt_checkbox_aceite',      'Li e aceito os termos de consentimento.'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_prazer_receber',       'it is a pleasure to welcome you to our space!'),
(1, 2, 'txt_verifique_detalhes',   'Please review your booking details below to make sure everything is just right:'),
(1, 2, 'label_acomodacao',         'Accommodation'),
(1, 2, 'label_qtd_pessoas',        'Number of Guests'),
(1, 2, 'label_checkin',            'Check-in Date'),
(1, 2, 'label_checkout',           'Check-out Date'),
(1, 2, 'txt_bloco_termo_seguranca','But we are not done yet... For your safety and that of all guests, please read and accept our Consent Terms and House Rules before proceeding. Your information is handled with full confidentiality, in compliance with applicable data protection regulations.'),
(1, 2, 'txt_checkbox_aceite',      'I have read and accept the consent terms.'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_prazer_receber',       'e um prazer recebermos voce em nosso espaco!'),
(2, 4, 'txt_verifique_detalhes',   'Verifique abaixo os detalhes da sua reserva para garantirmos que esta tudo do seu jeito:'),
(2, 4, 'label_acomodacao',         'Acomodacao'),
(2, 4, 'label_qtd_pessoas',        'Numero de Hospedes'),
(2, 4, 'label_checkin',            'Data de Check-in'),
(2, 4, 'label_checkout',           'Data de Check-out'),
(2, 4, 'txt_bloco_termo_seguranca','Mas ainda nao acabou... Para sua seguranca e de todos os hospedes, solicitamos que leia e aceite nosso Termo de Consentimento e Regulamento Interno antes de prosseguir. Suas informacoes sao tratadas com total sigilo, conforme a LGPD.'),
(2, 4, 'txt_checkbox_aceite',      'Li e aceito os termos de consentimento.'),

-- ============================================================
-- E. TELA DE SUCESSO DO CHECK-IN
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_titulo_sucesso_checkin', 'Check-in realizado com sucesso!'),
(1, 1, 'txt_desc_sucesso_checkin',   'Retire seu cartao e boa estadia.'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_titulo_sucesso_checkin', 'Check-in completed successfully!'),
(1, 2, 'txt_desc_sucesso_checkin',   'Please take your key card and enjoy your stay.'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_titulo_sucesso_checkin', 'Check-in realizado com sucesso!'),
(2, 4, 'txt_desc_sucesso_checkin',   'Retire seu cartao e boa estadia.'),

-- ============================================================
-- F. TELA DE RESUMO DO CHECKOUT E CONSUMOS
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_titulo_checkout',       'Checkout'),
(1, 1, 'txt_desc_revise_lancamentos','Revise seus lancamentos antes de finalizar.'),
(1, 1, 'label_coluna_item',         'Item'),
(1, 1, 'label_coluna_descricao',    'Descricao'),
(1, 1, 'label_coluna_valor',        'Valor Unit.'),
(1, 1, 'label_coluna_total',        'Total'),
(1, 1, 'txt_aviso_divergencia',     'Duvidas sobre o seu consumo? Caso encontre alguma divergencia, por favor, dirija-se a recepcao para que possamos verificar e corrigir antes de prosseguir.'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_titulo_checkout',       'Checkout'),
(1, 2, 'txt_desc_revise_lancamentos','Please review your charges before proceeding.'),
(1, 2, 'label_coluna_item',         'Item'),
(1, 2, 'label_coluna_descricao',    'Description'),
(1, 2, 'label_coluna_valor',        'Unit Price'),
(1, 2, 'label_coluna_total',        'Total'),
(1, 2, 'txt_aviso_divergencia',     'Questions about your charges? If you notice any discrepancy, please visit the front desk so we can review and correct it before you proceed.'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_titulo_checkout',       'Checkout'),
(2, 4, 'txt_desc_revise_lancamentos','Revise seus lancamentos antes de finalizar.'),
(2, 4, 'label_coluna_item',         'Item'),
(2, 4, 'label_coluna_descricao',    'Descricao'),
(2, 4, 'label_coluna_valor',        'Valor Unit.'),
(2, 4, 'label_coluna_total',        'Total'),
(2, 4, 'txt_aviso_divergencia',     'Duvidas sobre o seu consumo? Caso encontre alguma divergencia, por favor, dirija-se a recepcao para que possamos verificar e corrigir antes de prosseguir.'),

-- ============================================================
-- G. TELA DE PAGAMENTO E MAQUININHA
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_titulo_pagamento',      'Pagamento'),
(1, 1, 'btn_cartao_credito_debito', 'Cartao Credito/Debito'),
(1, 1, 'btn_gerar_pix',             'Gerar QrCode Pix'),
(1, 1, 'txt_titulo_insira_cartao',  'Insira ou Aproxime o cartao'),
(1, 1, 'txt_desc_insira_cartao',    'E siga as instrucoes exibidas no visor da maquininha'),
(1, 1, 'txt_titulo_escaneie_pix',   'Escaneie o QRCode'),
(1, 1, 'txt_desc_escaneie_pix',     'E siga as instrucoes exibidas no seu celular'),
(1, 1, 'label_total_a_pagar',       'Valor total a pagar'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_titulo_pagamento',      'Payment'),
(1, 2, 'btn_cartao_credito_debito', 'Credit / Debit Card'),
(1, 2, 'btn_gerar_pix',             'Generate Pix QrCode'),
(1, 2, 'txt_titulo_insira_cartao',  'Insert or Tap your card'),
(1, 2, 'txt_desc_insira_cartao',    'And follow the instructions on the card machine display'),
(1, 2, 'txt_titulo_escaneie_pix',   'Scan the QRCode'),
(1, 2, 'txt_desc_escaneie_pix',     'And follow the instructions on your mobile phone'),
(1, 2, 'label_total_a_pagar',       'Total amount due'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_titulo_pagamento',      'Pagamento'),
(2, 4, 'btn_cartao_credito_debito', 'Cartao Credito/Debito'),
(2, 4, 'btn_gerar_pix',             'Gerar QrCode Pix'),
(2, 4, 'txt_titulo_insira_cartao',  'Insira ou Aproxime o cartao'),
(2, 4, 'txt_desc_insira_cartao',    'E siga as instrucoes exibidas no visor da maquininha'),
(2, 4, 'txt_titulo_escaneie_pix',   'Escaneie o QRCode'),
(2, 4, 'txt_desc_escaneie_pix',     'E siga as instrucoes exibidas no seu celular'),
(2, 4, 'label_total_a_pagar',       'Valor total a pagar'),

-- ============================================================
-- H. TELA DE CHECKOUT CONCLUIDO (SUCESSO)
-- ============================================================

-- Grand Palace — Portugues (idioma_id=1)
(1, 1, 'txt_titulo_aprovado',             'Pagamento aprovado!!!'),
(1, 1, 'txt_desc_aprovado_compartimento', 'Foi um prazer te-lo conosco. Seu cartao foi desativado. Por favor, deposite-o no compartimento ao lado. Obrigado!'),

-- Grand Palace — English (idioma_id=2)
(1, 2, 'txt_titulo_aprovado',             'Payment approved!!!'),
(1, 2, 'txt_desc_aprovado_compartimento', 'It was a pleasure having you with us. Your key card has been deactivated. Please drop it in the slot beside this terminal. Thank you!'),

-- Pousada Vista Verde — Portugues (idioma_id=4)
(2, 4, 'txt_titulo_aprovado',             'Pagamento aprovado!!!'),
(2, 4, 'txt_desc_aprovado_compartimento', 'Foi um prazer te-lo conosco. Seu cartao foi desativado. Por favor, deposite-o no compartimento ao lado. Obrigado!');

-- =============================================================================
-- FIM DA MASSA DE DADOS
-- =============================================================================
