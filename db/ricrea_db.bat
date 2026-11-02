@echo off
rem ============================================================
rem  Ricrea il database da zero: cancella e ricrea le tabelle
rem  (schema.sql) e inserisce i prodotti di prova (dati_prova.sql).
rem  ATTENZIONE: tutti i dati esistenti vengono cancellati.
rem  Prerequisito: utente ecommerce_user e database ecommerce creati (db\crea_db.bat).
rem  Uso: db\ricrea_db.bat   (o doppio clic)
rem ============================================================
setlocal

rem --- trova psql: prima nel PATH, poi nelle cartelle standard di PostgreSQL ---
set "PSQL="
where psql >nul 2>nul && set "PSQL=psql"
if not defined PSQL for /d %%D in ("%ProgramFiles%\PostgreSQL\*") do if exist "%%D\bin\psql.exe" set "PSQL=%%D\bin\psql.exe"
if not defined PSQL (
    echo ERRORE: psql non trovato. Installa PostgreSQL oppure aggiungi la sua cartella bin al PATH.
    exit /b 1
)

set PGPASSWORD=ecommerce
set PGCLIENTENCODING=UTF8
set CONN=-U ecommerce_user -h 127.0.0.1 -d ecommerce -v ON_ERROR_STOP=1

echo [1/3] Creo le tabelle...
"%PSQL%" %CONN% -q -f "%~dp0schema.sql" 2>nul
if errorlevel 1 (echo ERRORE in schema.sql & exit /b 1)

echo [2/3] Inserisco i dati di prova...
"%PSQL%" %CONN% -q -f "%~dp0dati_prova.sql"
if errorlevel 1 (echo ERRORE in dati_prova.sql & exit /b 1)

echo [3/3] Prodotti presenti nel database:
"%PSQL%" %CONN% -c "SELECT id, nome, prezzo, iva, giacenza FROM prodotti ORDER BY id;"

endlocal
