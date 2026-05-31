# Fluxo de Telas — Totem FlexMedia

Detalhamento de cada tela, qual endpoint ela consome e o que esperar.

```
┌──────────┐    ┌──────┐    ┌────────┐    ┌──────┐
│  Splash  │───▶│ Info │───▶│ Idioma │───▶│ Home │
└──────────┘    └──────┘    └────────┘    └──┬───┘
                                              │
                                ┌─────────────┴──────────────┐
                                ▼                            ▼
                          ┌──────────┐                 ┌──────────┐
                          │ Check-in │                 │ Checkout │
                          └────┬─────┘                 └────┬─────┘
                               │                            │
                          ┌────▼─────┐                 ┌────▼─────┐
                          │  Ident   │                 │  Ident   │
                          └────┬─────┘                 └────┬─────┘
                               │                            │
                          ┌────▼─────┐                 ┌────▼─────┐
                          │ Aguarde  │                 │ Aguarde  │
                          └────┬─────┘                 └────┬─────┘
                               │                            │
                          ┌────▼─────┐                 ┌────▼─────┐
                          │  Resumo  │                 │ Extrato  │
                          │  + Termo │                 └────┬─────┘
                          └────┬─────┘                      │
                               │                       ┌────▼─────┐
                          ┌────▼─────┐                 │ Pagamento│
                          │Codifican-│                 └────┬─────┘
                          │  do      │                      │
                          └────┬─────┘                ┌─────┴─────┐
                               │                      ▼           ▼
                          ┌────▼─────┐           ┌──────┐    ┌──────┐
                          │  Sucesso │           │Cartão│    │ PIX  │
                          └──────────┘           └──┬───┘    └──┬───┘
                                                    │           │
                                                    ▼           ▼
                                                ┌────────────────┐
                                                │ Aguarde Pagto  │
                                                └────────┬───────┘
                                                         ▼
                                                   ┌──────────┐
                                                   │  Sucesso │
                                                   └──────────┘
```

## Telas comuns

### 1. Splash (`s-splash`)
- Tela de boas-vindas com logo do hotel
- Background usa `splashImagePath` se vier da API
- Aplica `corPrimariaHex` e `corSecundariaHex` como gradiente de fundo
- **Endpoint:** `GET /totem/inicializacao?idiomaIso={iso}` (chamado no boot)

### 2. Info / Lazer (`s-info`)
- Apresentação do hotel
- Texto vem de `infoHotelTexto`
- Imagem opcional via `infoHotelImagePath`

### 3. Idioma (`s-idioma`)
- Lista de idiomas disponíveis
- Ao trocar de idioma, **re-chama** `/totem/inicializacao` para puxar o novo dicionário

### 4. Home (`s-home`)
- Menu principal: Check-in ou Check-out

## Fluxo de Check-in

### 5. Identificação (`s-ident`)
- Campos: código da reserva + CPF/passaporte
- Validação no frontend (campos obrigatórios)

### 6. Aguarde Busca (`s-aguarde-busca`)
- **Endpoint:** `GET /totem/reservas/busca?codigo=&documento=`
- Loading spinner enquanto a API responde

### 7. Resumo Check-in (`s-resumo-checkin`)
- Mostra acomodação, data de entrada/saída, quantidade de hóspedes
- Saudação personalizada com o primeiro nome do hóspede
- **Termo LGPD obrigatório** — botão "Continuar" desabilitado até marcar o checkbox

### 8. Codificando (`s-codificando`)
- **Endpoint:** `POST /totem/checkin` com `termoConsentimentoAceito: true`
- Após sucesso, tenta extrair `numeroQuarto` da resposta
- Se não vier, faz fallback chamando `GET /totem/reservas/busca` novamente

### 9. Sucesso Check-in (`s-sucesso-checkin`)
- Exibe o número do quarto alocado

## Fluxo de Check-out

### 5b. Identificação (mesma tela `s-ident`)
- Distinção é via `state.flow = 'checkout'`

### 6b. Aguarde Busca
- **Endpoint:** `GET /totem/extrato?codigo=&documento=`

### 7b. Extrato (`s-resumo-checkout`)
- Tabela com lançamentos (diárias, frigobar, room service)
- Total calculado no frontend a partir dos itens (ou usa `valorTotal` se vier da API)

### 8b. Pagamento (`s-pagamento`)
- Escolha entre Cartão Crédito/Débito ou PIX

### 9b. Maquininha (`s-cartao`) OU QR Code (`s-pix`)
- Animação visual
- Botão "Simular pagamento" para avançar (em produção viria de hardware/banco)

### 10b. Aguarde Pagamento (`s-aguarde-pagto`)
- **Endpoint:** `POST /totem/checkout` com `metodoPagamento` e `valorPago`

### 11b. Sucesso Check-out (`s-sucesso-checkout`)
- Confirmação final com mensagem de despedida

## Tratamento defensivo de respostas

O frontend foi escrito para sobreviver a pequenas variações no formato JSON do backend. A função `pick()` tenta múltiplos nomes para cada campo:

```js
const nome = pick(reserva, 'nomeHospede', 'nome_hospede');
const acomod = pick(reserva, 'acomodacao', 'tipoQuarto', 'numeroQuarto');
const consumos = pick(reserva, 'consumos', 'consumosEstadia', 'lancamentos');
```

Isso significa que se o backend serializar em `camelCase` (padrão Spring) ou `snake_case` (caso alguém configure `PropertyNamingStrategy.SNAKE_CASE`), ambos funcionam sem alteração no front.
