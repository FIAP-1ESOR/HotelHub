@echo off
REM FlexMedia Hotel Hub — Totem Frontend
REM Script de inicialização rápida (Windows)

set PORT=5500

echo ===========================================
echo  FlexMedia Hotel Hub — Totem Frontend
echo ===========================================
echo.

REM Tenta Python
where python >nul 2>nul
if %errorlevel%==0 (
    echo Servindo com Python na porta %PORT%...
    echo Abra: http://localhost:%PORT%
    echo.
    echo Pressione Ctrl+C para parar.
    python -m http.server %PORT%
    goto :eof
)

REM Tenta Node/npx
where npx >nul 2>nul
if %errorlevel%==0 (
    echo Servindo com Node/serve na porta %PORT%...
    echo Abra: http://localhost:%PORT%
    echo.
    npx serve -p %PORT%
    goto :eof
)

REM Tenta PHP
where php >nul 2>nul
if %errorlevel%==0 (
    echo Servindo com PHP na porta %PORT%...
    echo Abra: http://localhost:%PORT%
    echo.
    php -S localhost:%PORT%
    goto :eof
)

echo Nenhum servidor encontrado (python, node, php).
echo.
echo Abra o index.html diretamente no navegador, ou instale uma das opcoes:
echo   - Python:  https://python.org
echo   - Node.js: https://nodejs.org
echo.
pause
