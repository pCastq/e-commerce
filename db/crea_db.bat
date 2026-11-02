@echo off
rem ============================================================
rem  Crea l'utente ecommerce_user e il database ecommerce.
rem  Da eseguire UNA volta, subito dopo aver installato PostgreSQL.
rem  Chiede la password dell'utente "postgres" (scelta durante l'installazione di PostgreSQL).
rem  Uso: db\crea_db.bat   (o doppio clic)
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

set PGCLIENTENCODING=UTF8
"%PSQL%" -U postgres -h 127.0.0.1 -d postgres -v ON_ERROR_STOP=1 -f "%~dp0crea_db.sql"
if errorlevel 1 (echo ERRORE: utente/database non creati. Controlla la password di postgres. & exit /b 1)

echo.
echo Fatto. Ora esegui db\ricrea_db.bat per creare le tabelle e i dati di prova.
endlocal
