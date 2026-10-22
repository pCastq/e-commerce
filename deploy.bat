@echo off
setlocal
rem ============================================================
rem  Compila il progetto e lo copia in Tomcat (webapps\ecommerce)
rem  Uso: deploy.bat          (poi avvia/riavvia Tomcat se serve)
rem ============================================================

set ROOT=%~dp0
set TOMCAT=%ROOT%apache-tomcat-10.1.61
set STAGE=%ROOT%out\app
set TARGET=%TOMCAT%\webapps\ecommerce

if not exist "%TOMCAT%\lib\servlet-api.jar" (
    echo ERRORE: Tomcat non trovato in %TOMCAT%
    exit /b 1
)

echo [1/3] Preparo la cartella temporanea...
if exist "%STAGE%" rmdir /s /q "%STAGE%"
mkdir "%STAGE%\WEB-INF\classes"
robocopy "%ROOT%web" "%STAGE%" /E /NFL /NDL /NJH /NJS >nul
if errorlevel 8 (echo ERRORE nella copia di web\ & exit /b 1)

echo [2/3] Compilo i sorgenti Java...
dir /s /b "%ROOT%src\*.java" > "%ROOT%out\sources.txt"
javac --release 21 -encoding UTF-8 -cp "%TOMCAT%\lib\servlet-api.jar;%STAGE%\WEB-INF\lib\*" -d "%STAGE%\WEB-INF\classes" @"%ROOT%out\sources.txt"
if errorlevel 1 (echo ERRORE di compilazione & exit /b 1)

echo [3/3] Copio in Tomcat...
robocopy "%STAGE%" "%TARGET%" /MIR /NFL /NDL /NJH /NJS >nul
if errorlevel 8 (echo ERRORE nella copia verso Tomcat & exit /b 1)

echo.
echo Fatto. Apri http://localhost:8080/ecommerce/
endlocal
