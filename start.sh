#!/bin/bash
# FlexMedia Hotel Hub — Totem Frontend
# Script de inicialização rápida (Linux/macOS)

PORT=5500

echo "==========================================="
echo " FlexMedia Hotel Hub — Totem Frontend"
echo "==========================================="
echo ""

# Detecta Python ou Node disponíveis
if command -v python3 &> /dev/null; then
    echo "→ Servindo com Python na porta $PORT..."
    echo "→ Abra: http://localhost:$PORT"
    echo ""
    echo "Pressione Ctrl+C para parar."
    python3 -m http.server $PORT
elif command -v python &> /dev/null; then
    echo "→ Servindo com Python na porta $PORT..."
    echo "→ Abra: http://localhost:$PORT"
    echo ""
    python -m http.server $PORT
elif command -v npx &> /dev/null; then
    echo "→ Servindo com Node/serve na porta $PORT..."
    echo "→ Abra: http://localhost:$PORT"
    echo ""
    npx serve -p $PORT
elif command -v php &> /dev/null; then
    echo "→ Servindo com PHP na porta $PORT..."
    echo "→ Abra: http://localhost:$PORT"
    echo ""
    php -S localhost:$PORT
else
    echo "⚠ Nenhum servidor encontrado (python, node, php)."
    echo ""
    echo "Abra o index.html diretamente no navegador, ou instale uma das opções:"
    echo "  - Python:  https://python.org"
    echo "  - Node.js: https://nodejs.org"
    echo ""
    exit 1
fi
