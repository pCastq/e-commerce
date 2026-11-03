# ShopTSW – e-commerce (Tecnologie Software per il Web)

Piattaforma e-commerce: Java Servlet + JSP su Apache Tomcat 10.1, PostgreSQL,
architettura MVC, HTML5/CSS3/JavaScript, AJAX con JSON.

#DEMO 
https://www.youtube.com/watch?v=GHzE69yeZd8

## Requisiti


| Programma | Versione | Note |
|---|---|---|
| **JDK** (Java Development Kit) | **21** o superiore | serve il JDK, non solo il JRE (per `javac`). Per esempio Eclipse Temurin: <https://adoptium.net> |
| **Apache Tomcat** | **10.1.61** | già incluso nel progetto (cartella `apache-tomcat-10.1.61`); lo zip serve solo se la cartella manca: zip "Windows x64": <https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.61/bin/apache-tomcat-10.1.61-windows-x64.zip> |
| **PostgreSQL** | **18** (va bene anche una versione vicina) | in locale, porta 5432 predefinita. Durante l'installazione si sceglie la **password dell'utente `postgres`**: serve al passo 2 |

Non serve installare altro: il driver JDBC di PostgreSQL viene scaricato in automatico da `deploy.bat`.
La porta **8080** deve essere libera.

## Installazione passo passo

### 1. Scaricare il progetto
```bash
git clone https://github.com/pCastq/e-commerce
cd e-commerce
```

### 2. Creare il database (una sola volta)
```bat
db\crea_db.bat
```
- Cerca `psql.exe` (nel `PATH` o in `C:\Program Files\PostgreSQL\*\bin`).
- Chiede la **password dell'utente `postgres`** (quella scelta installando PostgreSQL).
- Crea l'utente `ecommerce_user` (password `ecommerce`) e il database `ecommerce`, solo se non esistono già.

### 3. Creare le tabelle e i dati di prova
```bat
db\ricrea_db.bat
```
- Esegue `schema.sql`: elimina e ricrea le tabelle (colonne, tipi, vincoli).
- Poi esegue `dati_prova.sql`: inserisce i prodotti di prova e l'utente amministratore.
- **Attenzione:** cancella tutti i dati esistenti. Si può rilanciare per ripartire da zero.

### 4. Compilare e pubblicare il sito
```bat
deploy.bat
```
- Prepara le cartelle di Tomcat (`temp`, `logs`) e, se manca, scarica il driver di PostgreSQL in `apache-tomcat-10.1.61\lib`
  (il `.jar` serve a Tomcat per collegarsi al database).
- Ricrea la cartella di appoggio `out\app`, ci copia tutto il contenuto di `web/` e compila tutti i `.java` di `src/`
  in `out\app\WEB-INF\classes` (dove Tomcat si aspetta le classi).
- Copia il risultato (con `robocopy /MIR`, che rispecchia l'origine) in `apache-tomcat-10.1.61\webapps\ecommerce`.
"

### 5. Avviare Tomcat
Prima indica dov'è Java (se `JAVA_HOME` non è già impostata):
```bat
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.x-hotspot
```
poi:
```bat
apache-tomcat-10.1.61\bin\startup.bat
```
(`startup.sh` su Linux/macOS). Aspetta la riga `Server startup in [...] milliseconds` e apri **<http://localhost:8080/ecommerce/>**.
Per fermarlo: `apache-tomcat-10.1.61\bin\shutdown.bat`.

Dopo ogni modifica al codice: `deploy.bat` e, se serve, riavviare Tomcat.

## Account di prova

- **Amministratore:** `admin@email.it` / `Admin2026!` (area `/admin`: prodotti e ordini)
- **Clienti:** ci si registra dalla pagina *Registrati*.

Credenziali del database (`ecommerce_user` / `ecommerce`): in `web/META-INF/context.xml`. Solo per sviluppo.

## Immagini dei prodotti

Nel database si salva **solo il nome del file** (es. `detersivo.jpg`). Il file deve stare in `web/img/prodotti/` con lo stesso nome.
Le JSP compongono l'indirizzo, e il browser scarica l'immagine direttamente da Tomcat:
```jsp
<img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${prodotto.immagineFile}'/>">
```
Se il prodotto non ha immagine, si mostra `nessuna.svg`. Per un nuovo prodotto: copiare il file in `web/img/prodotti/`, rilanciare `deploy.bat` e poi inserire il nome nel form admin.

## Se qualcosa non funziona

| Sintomo | Causa probabile |
|---|---|
| `Neither the JAVA_HOME nor the JRE_HOME ...` | impostare `JAVA_HOME` sul JDK 21 (passo 5) |
| `psql non trovato` | PostgreSQL non installato o non nel `PATH` |
| `crea_db.bat`: autenticazione fallita | password di `postgres` sbagliata |
| Errore 500 / pagina di errore | PostgreSQL spento, database non creato (passi 2-3) o driver mancante in `lib` |
| `Address already in use` | porta 8080 occupata, o Tomcat già avviato |
| Errore di compilazione con `release 21` | JDK più vecchio del 21 |

## Struttura

- `src/control` servlet (solo HTTP) · `src/model` bean · `src/model/service` logica · `src/model/dao` accesso al database
- `src/filter` filtri · `src/util` classi di supporto
- `web/WEB-INF/views` pagine JSP · `web/WEB-INF/fragments` header, menu, footer · `web/css`, `web/js`, `web/img`
- `db/` script SQL e `.bat` del database · `deploy.bat` compila e pubblica in Tomcat
