@echo off
rem ============================================================
rem  Ricrea il database da zero: cancella e ricrea le tabelle
rem  (schema.sql) e inserisce i prodotti di prova (dati_prova.sql).
rem  ATTENZIONE: tutti i dati esistenti vengono cancellati.
rem  Prerequisito: utente ecommerce_user e database ecommerce creati.
rem  Uso: db\ricrea_db.bat   (o doppio clic)
rem ============================================================
setlocal

set PSQL="C:\Program Files\PostgreSQL\18\bin\psql.exe"
set PGPASSWORD=ecommerce
set PGCLIENTENCODING=UTF8
set CONN=-U ecommerce_user -h 127.0.0.1 -d ecommerce -v ON_ERROR_STOP=1

if not exist %PSQL% (
    echo ERRORE: psql non trovato. Controlla il percorso in questo file.
    exit /b 1
)

echo [1/3] Creo le tabelle...
%PSQL% %CONN% -q -f "%~dp0schema.sql" 2>nul
if errorlevel 1 (echo ERRORE in schema.sql & exit /b 1)

echo [2/3] Inserisco i dati di prova...
%PSQL% %CONN% -q -f "%~dp0dati_prova.sql"
if errorlevel 1 (echo ERRORE in dati_prova.sql & exit /b 1)

echo [3/3] Prodotti presenti nel database:
%PSQL% %CONN% -c "SELECT id, nome, prezzo, iva, giacenza FROM prodotti ORDER BY id;"

endlocal
