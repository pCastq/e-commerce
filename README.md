# ShopTSW – e-commerce (Tecnologie Software per il Web)

Piattaforma e-commerce: Java Servlet + JSP su Apache Tomcat 10.1, PostgreSQL,
architettura MVC (`control` → `model/service` → `model/dao`), HTML5/CSS3/JavaScript, AJAX con JSON.

## Requisiti


| Programma | Versione | Note |
|---|---|---|
| **JDK** (Java Development Kit) | **21** o superiore | serve il JDK, non solo il JRE (per `javac`). Per esempio Eclipse Temurin: <https://adoptium.net> |
| **Apache Tomcat** | **10.1.61** | zip "Windows x64": <https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.61/bin/apache-tomcat-10.1.61-windows-x64.zip> |
| **PostgreSQL** | **18** (va bene anche una versione vicina) | in locale, porta 5432 predefinita. Durante l'installazione si sceglie la **password dell'utente `postgres`**: serve al passo 3 |

Non serve installare altro: il driver JDBC di PostgreSQL viene scaricato in automatico da `deploy.bat` (serve internet la prima volta).
La porta **8080** deve essere libera.

## Come avviare il progetto (da zero, passo per passo)

1. **Scaricare il progetto**: `git clone https://github.com/pCastq/e-commerce` (oppure *Code → Download ZIP* e decomprimerlo).
   Nei passi seguenti "cartella del progetto" è la cartella `e-commerce` che contiene `deploy.bat`.
2. **Mettere Tomcat nella cartella del progetto**: decomprimere lo zip di Tomcat in modo che esista la cartella
   `e-commerce\apache-tomcat-10.1.61\` (dentro ci devono essere `bin`, `lib`, `webapps`...).
3. **Creare utente e database** (una sola volta): doppio clic su `db\crea_db.bat` (o dal prompt: `db\crea_db.bat`).
   Chiede la password dell'utente `postgres` scelta durante l'installazione di PostgreSQL.
4. **Creare le tabelle e i dati di prova**: `db\ricrea_db.bat`. Ripetibile in qualsiasi momento: **cancella tutti i dati** e riparte da zero.
5. **Compilare e copiare il sito in Tomcat**: `deploy.bat` dalla cartella del progetto.
   Al primo avvio scarica il driver di PostgreSQL in `apache-tomcat-10.1.61\lib`."
6. **Dire a Tomcat dov'è Java** (una volta per finestra del prompt, se `JAVA_HOME` non è già impostata):
   ```bat
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.x-hotspot
   ```
   (il percorso è quello dove è installato il JDK 21).
7. **Avviare Tomcat**: `apache-tomcat-10.1.61\bin\startup.bat`. Si apre una finestra con il log: attendere la riga
   `Server startup in [...] milliseconds`.
8. **Aprire il sito**: <http://localhost:8080/ecommerce/>

Per **fermare** Tomcat: `apache-tomcat-10.1.61\bin\shutdown.bat`.
Dopo ogni modifica al codice basta rieseguire `deploy.bat` (e, se serve, riavviare Tomcat).

## Account di prova

- **Amministratore**: `admin@email.it` / `Admin2026!` (accede all'area `/admin`: prodotti e ordini)
- **Clienti**: ci si registra dalla pagina *Registrati* (la password richiede maiuscola, minuscola, cifra e un simbolo tra `. , _ - ! ? @`, minimo 8 caratteri).

Le credenziali del database (`ecommerce_user` / `ecommerce`) sono in `web/META-INF/context.xml` e in `db/crea_db.sql`: sono solo per lo sviluppo.

## Se qualcosa non funziona

| Sintomo | Causa probabile |
|---|---|
| `Neither the JAVA_HOME nor the JRE_HOME environment variable is defined` | passo 6: impostare `JAVA_HOME` sul JDK 21 |
| `deploy.bat`: `ERRORE: Tomcat non trovato` | la cartella `apache-tomcat-10.1.61` non è dentro la cartella del progetto (passo 2) |
| `deploy.bat`: errore di compilazione, `release 21 non supportata` | JDK più vecchio del 21 |
| `psql non trovato` | PostgreSQL non installato o non nel `PATH`: reinstallare o aggiungere la cartella `bin` di PostgreSQL al `PATH` |
| `crea_db.bat`: autenticazione fallita | password dell'utente `postgres` sbagliata |
| Il sito risponde con errore 500 / pagina di errore | PostgreSQL spento, database non creato (passi 3-4) o driver mancante in `apache-tomcat-10.1.61\lib` |
| `Address already in use` all'avvio di Tomcat | la porta 8080 è occupata da un altro programma (o Tomcat è già avviato) |

## Struttura

- `src/control` servlet (solo HTTP) · `src/model` bean · `src/model/service` logica di business · `src/model/dao` accesso al database
- `src/filter` filtri (codifica, login, area admin) · `src/util` classi di supporto
- `web/WEB-INF/views` pagine JSP · `web/WEB-INF/fragments` header, menu, footer · `web/css`, `web/js`, `web/img`
- `db/` script e SQL del database · `deploy.bat` compila e copia in Tomcat
