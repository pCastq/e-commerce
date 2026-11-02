# ShopTSW – e-commerce (Tecnologie Software per il Web)

Sito di commercio elettronico: Java Servlet + JSP su Apache Tomcat 10.1, PostgreSQL con connection pool (DataSource JNDI),
architettura MVC (`control` → `model/service` → `model/dao`), HTML5/CSS3/JavaScript, AJAX con JSON.

## Avvio

1. **Database** (PostgreSQL, utente `ecommerce_user` e database `ecommerce` già creati): eseguire `db\ricrea_db.bat`
   (crea le tabelle e inserisce prodotti e utenti di prova; cancella i dati esistenti).
2. **Compilazione e copia in Tomcat**: `deploy.bat`.
3. **Avvio**: `apache-tomcat-10.1.61\bin\startup.bat`, poi aprire <http://localhost:8080/ecommerce/>.

Account di prova (solo sviluppo): amministratore `admin@email.it` / `Admin2026!`; i clienti si registrano dal sito.

## Struttura

- `src/control` servlet (solo HTTP) · `src/model` bean · `src/model/service` logica di business · `src/model/dao` accesso al database
- `src/filter` filtri (codifica, login, area admin) · `src/util` classi di supporto
- `web/WEB-INF/views` pagine JSP · `web/WEB-INF/fragments` header, menu, footer · `web/css`, `web/js`, `web/img`
- `db/` schema e dati di prova
