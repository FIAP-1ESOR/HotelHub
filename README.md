# FlexMedia Hotel Hub — Totem (Frontend)

Frontend do **módulo Totem** do FlexMedia Hotel Hub. Tela única (`index.html`) que simula o painel de autoatendimento físico do hotel, consumindo a API REST do backend Spring Boot.

> **TL;DR para rodar:** abra `index.html` no navegador. Se o backend estiver rodando em `localhost:8080`, conecta automaticamente. Se não estiver, cai em modo mock e funciona para demonstração.

---

## 📋 Sumário

- [Como rodar](#-como-rodar)
- [Modo online (com backend)](#-modo-online-com-backend)
- [Modo mock (sem backend)](#-modo-mock-sem-backend)
- [Fluxos disponíveis](#-fluxos-disponíveis)
- [Dados de teste](#-dados-de-teste)
- [Endpoints consumidos](#-endpoints-consumidos)
- [White Label / Multi-tenant](#-white-label--multi-tenant)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Troubleshooting](#-troubleshooting)
- [Customizações comuns](#-customizações-comuns)

---

## 🚀 Como rodar

### Opção 1 — Abrir direto no navegador (mais simples)

```bash
# Windows
start index.html

# macOS
open index.html

# Linux
xdg-open index.html
```

Ou simplesmente **clique duplo** no `index.html`.

### Opção 2 — Servir com um web server local (recomendado se for testar com o backend)

Se você abrir o arquivo direto via `file://`, alguns navegadores bloqueiam o `fetch()` para o `localhost:8080` por CORS. Para evitar isso:

```bash
# Com Python (já instalado na maioria das máquinas)
python -m http.server 5500

# Com Node.js
npx serve -p 5500

# Com PHP
php -S localhost:5500
```

Depois acesse: **http://localhost:5500**

> **VSCode:** instale a extensão "Live Server" e clique em "Go Live" no canto inferior direito.

---

## 🌐 Modo online (com backend)

Para a integração funcionar, o backend Spring Boot precisa estar rodando.

### Passos:

1. Em outro terminal, vá até a pasta do backend e rode:
   ```bash
   mvn spring-boot:run
   ```

2. Verifique se a API responde:
   ```bash
   curl http://localhost:8080/totem/inicializacao?idiomaIso=pt -H "X-Hotel-ID: 1"
   ```

3. Abra o `index.html` no navegador. Se tudo estiver OK, a barra superior do totem mostrará:
   ```
   ● API online | base_url http://localhost:8080 | X-Hotel-ID 1
   ```

### CORS

Se o backend não permitir requisições do frontend (erro de CORS no console), peça ao backend para incluir o seguinte em alguma `@Configuration`:

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*");
        }
    };
}
```

> Para produção, restrinja `allowedOrigins` ao domínio real.

---

## 🧪 Modo mock (sem backend)

Se a API não responder em até alguns segundos, o totem automaticamente entra em **modo mock** — todos os fluxos funcionam usando dados estáticos baseados no `data.sql` original. A barra superior ficará vermelha indicando o modo offline.

Útil para:
- **Demonstrações** sem precisar subir Java
- **Trabalhar no front** quando o back está com problema
- **Apresentação rápida** em outra máquina

---

## 🔁 Fluxos disponíveis

### 1. Splash → Lazer → Idioma → Home

Tela inicial → tela de lazer/info do hotel → seleção de idioma (PT/EN) → menu com Check-in/Check-out.

### 2. Check-in

1. **Identificação** — código da reserva + CPF/passaporte
2. **Aguarde** — busca da reserva na API
3. **Resumo** — dados da reserva + termo LGPD obrigatório
4. **Codificando** — animação de processamento
5. **Sucesso** — número do quarto alocado

### 3. Check-out

1. **Identificação** — código da reserva + CPF/passaporte
2. **Aguarde** — busca do extrato
3. **Extrato** — lançamentos da estadia + total
4. **Pagamento** — escolha entre Cartão ou Pix
5. **Maquininha** (cartão) ou **QR Code** (Pix)
6. **Sucesso** — pagamento aprovado

---

## 🧾 Dados de teste

Já preenchidos como dica na tela de identificação:

| Fluxo | Código Reserva | Documento | Hotel ID |
|---|---|---|---|
| Check-in | `GP-2025-003` | `567.890.123-45` | 1 |
| Check-out | `GP-2025-001` | `345.678.901-23` | 1 |
| Check-out alternativo | `VV-2025-001` | `AB123456` | 2 |

> Estes códigos espelham o `data.sql` do backend. Se o backend tiver outros dados, ajuste o seed ou use as credenciais reais do banco.

---

## 🔌 Endpoints consumidos

O frontend consome **apenas o Módulo 1 (Totem)** da API. Todos exigem header `X-Hotel-ID`.

| Método | Endpoint | Uso |
|---|---|---|
| `GET` | `/totem/inicializacao?idiomaIso={iso}` | White label + dicionário ao iniciar |
| `GET` | `/totem/reservas/busca?codigo=&documento=` | Passo 1 do check-in |
| `POST` | `/totem/checkin` | Passo 2 do check-in |
| `GET` | `/totem/extrato?codigo=&documento=` | Passo 1 do check-out |
| `POST` | `/totem/checkout` | Passo 2 do check-out |

### Payloads

**POST /totem/checkin:**
```json
{
  "codigoReserva": "GP-2025-003",
  "documentoCpfPassaporte": "567.890.123-45",
  "termoConsentimentoAceito": true
}
```

**POST /totem/checkout:**
```json
{
  "reservaId": 1,
  "metodoPagamento": "PIX",
  "valorPago": 1789.00
}
```

Valores aceitos em `metodoPagamento`: `PIX`, `CARTAO_CREDITO`, `CARTAO_DEBITO`.

---

## 🎨 White Label / Multi-tenant

A barra superior do totem permite trocar o `X-Hotel-ID` em tempo de execução para testar o white label de cada hotel.

- **Hotel 1 — Grand Palace** → tema azul (`#1a56db`)
- **Hotel 2 — Vista Verde** → tema verde (`#16a34a`)

O endpoint `/totem/inicializacao` retorna `corPrimariaHex` e `corSecundariaHex`, e o frontend aplica via CSS Custom Properties. O `logoPath` e o `splashImagePath` são montados a partir do `base_url + path` retornado pela API.

---

## 📁 Estrutura do projeto

```
flexmedia-totem-frontend/
├── index.html         # Aplicação completa (HTML + CSS + JS em um único arquivo)
├── README.md          # Este arquivo
└── docs/
    ├── FLUXO.md       # Detalhes de cada tela e o que ela consome
    └── DEBUG.md       # Como usar o painel de debug e atalhos
```

> **Por que um único `index.html`?** O totem é uma aplicação enxuta, single-page, sem build step. Mantemos tudo num único arquivo para facilitar o deploy (basta servir o arquivo) e a inspeção pelos colegas de equipe.

---

## 🛠 Troubleshooting

### A barra superior fica vermelha ("API offline")

- Verifique se o Spring Boot está rodando (`mvn spring-boot:run`)
- Teste manualmente: `curl http://localhost:8080/totem/inicializacao?idiomaIso=pt -H "X-Hotel-ID: 1"`
- Se a porta for diferente, mude no campo `base_url` da barra superior
- Verifique CORS no console do navegador (F12 → Console)

### Erro 404 ao buscar reserva

- Os dados podem não estar no banco. Conferir se o `data.sql` foi executado pelo Spring na inicialização (logs do backend)
- Conferir se o `X-Hotel-ID` selecionado corresponde ao hotel da reserva

### A imagem do logo / splash não aparece

- Os paths retornados pela API são relativos (`uploads/logos/...`). O frontend monta como `${base_url}/${path}`
- Se a imagem falhar, o frontend usa fallback automático com a primeira letra do nome do hotel
- Confirme que o backend está servindo a pasta `flexmedia_uploads` corretamente

### Botão "Continuar" no resumo de check-in fica desabilitado

- É o comportamento esperado. O usuário precisa marcar o checkbox de aceite do termo (LGPD) primeiro.

### Os textos não traduzem ao mudar idioma

- A tradução vem do `dicionario` retornado pelo endpoint `/totem/inicializacao`. Se a chave não existir no dicionário, o texto fallback em português permanece.

---

## ✏️ Customizações comuns

### Mudar a URL padrão da API

Edite a linha que define o input `api-url`:

```html
<input type="text" id="api-url" value="http://localhost:8080" />
```

### Adicionar um novo idioma

1. Garanta que o backend retorne o dicionário no novo idioma
2. Adicione a bandeira no objeto `flags` do JS:
   ```js
   const flags = { pt: '🇧🇷', en: '🇺🇸', es: '🇪🇸', fr: '🇫🇷', /* novo */ de: '🇩🇪' };
   ```

### Mudar tempos de espera (modo mock)

Procure as chamadas `await sleep(...)` no JS. Os valores estão em milissegundos.

### Desabilitar o painel de debug em produção

Remova a `<div class="debug-panel">` no final do HTML.

---

## 📚 Documentação relacionada

- `Documentac_a_o_da_API__FlexMedia_Hotel_Hub.pdf` — contratos completos da API (Totem, Admin, Master)
- `HotelHub_Endpoints_no_Postman.json` — coleção Postman para testar a API
- `schema.sql` — modelo de dados do H2
- `data.sql` — seed com hotéis, reservas e consumos de exemplo

---

**Equipe FlexMedia · 2026**
