# Painel de Debug

O totem tem dois recursos de debug visíveis em tela para facilitar o desenvolvimento e a apresentação.

## 1. Barra superior (status da API)

```
● API online | base_url http://localhost:8080 | X-Hotel-ID [1 ▼] ↻
```

### Componentes

- **Bolinha de status:**
  - 🟢 Verde — API respondeu, modo online
  - 🟠 Laranja — Conectando
  - 🔴 Vermelho — API offline, usando mock

- **base_url:** URL do backend Spring Boot. Editável em tempo de execução. Útil se o backend estiver em outra porta ou em outro host (ex: máquina do colega na mesma rede).

- **X-Hotel-ID:** Seletor para trocar entre os hotéis cadastrados. Útil para demonstrar o **white label multi-tenant** ao vivo.

- **↻ (recarregar):** Re-executa o boot completo (chama `/totem/inicializacao` de novo, recarrega cores, dicionário e estado).

## 2. Painel inferior (Debug / Dev tools)

```
🛠 DEBUG / DEV TOOLS
Hotel: Grand Palace Hotel (id=1) | Idioma: pt | Modo: MOCK

[splash] [info] [idioma] [home] [ident] [resumo-ci]
[ok-ci] [resumo-co] [pagto] [pix] [ok-co]
```

### Para que serve

- **Linha de status:** mostra o estado atual da aplicação (hotel selecionado, idioma ativo, modo online/mock).

- **Botões de navegação rápida:** permitem pular para qualquer tela sem precisar fazer o fluxo inteiro. Útil para:
  - Demonstração rápida de uma tela específica
  - Debug visual de uma tela isolada
  - Apresentação de portfólio

> ⚠️ Os botões de navegação **não passam dados de contexto**. Por exemplo, pular direto para "ok-ci" não mostrará o número do quarto, porque nenhum check-in foi efetivado. Use-os apenas para inspeção visual.

## Como remover o painel de debug em produção

Se for fazer deploy em produção (totem físico), remova:

1. A `<div class="api-bar">` no topo do `<body>`
2. A `<div class="debug-panel">` no final do `<body>`
3. Os estilos CSS `.api-bar`, `.debug-panel`, `.fnav`, `.debug-data` (opcional, mas reduz o tamanho)

Alternativa: envolver em um bloco condicional baseado em URL:

```js
if (!location.search.includes('debug=true')) {
  document.querySelector('.api-bar').style.display = 'none';
  document.querySelector('.debug-panel').style.display = 'none';
}
```

Assim o debug só aparece quando a URL tem `?debug=true`.

## Atalhos úteis no console (F12)

Para testar manualmente no DevTools:

```js
// Forçar um fluxo
state.flow = 'checkout';
go('s-ident');

// Inspecionar reserva atual
console.log(state.reservaAtual);

// Trocar idioma sem usar a tela
state.idiomaIso = 'en';
initHotel();

// Ver o que a API retornou na inicialização
console.log(state.whiteLabel);

// Ver o dicionário atual
console.log(state.dicionario);
```
