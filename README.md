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

---

# Traccia del progetto



Descrizione Progetto
Il progetto d’esame consiste nella realizzazione di un sito di commercio elettronico completo e funzionante.
Il progetto è di gruppo (massimo tre persone) e prevede la progettazione e lo sviluppo di un’applicazione
web che copra tutti gli aspetti studiati durante il corso: dalla strutturazione del database alla realizzazione
dell’interfaccia utente, passando per la logica di business lato server.
Sebbene sia possibile suddividersi i compiti durante lo sviluppo, al momento della discussione ogni
componente del gruppo deve essere a conoscenza dei contenuti e delle funzionalità dell’intero progetto.



Tecnologie Richieste
Il progetto deve essere sviluppato utilizzando il seguente stack tecnologico:
• Java Servlet e JSP come tecnologie server-side
• Apache Tomcat come application server (il sito deve girare direttamente su Tomcat)
• Database relazionale (MySQL/PostgreSQL) con gestione tramite DataSource o DriverManager con
Connection Pool
• HTML5, CSS3 e JavaScript per il front-end
• AJAX per le comunicazioni asincrone
• Pattern MVC come architettura software



Funzionalità Area Cliente
• Registrazione utente con validazione dei campi tramite espressioni regolari
• Login e gestione della sessione
• Navigazione del catalogo prodotti con visualizzazione dettagliata
• Barra di ricerca con suggerimenti AJAX
• Gestione del carrello: aggiunta prodotti, modifica quantità, rimozione
• Conferma ordine con svuotamento del carrello
• Visualizzazione dello storico ordini effettuati
• Messaggi di conferma per le azioni eseguite (es. registrazione, ordine effettuato)



Funzionalità Area Amministratore
L’accesso all’area di amministrazione deve essere protetto tramite autenticazione programmata.
L’amministratore deve poter:
• Inserire nuovi prodotti nel catalogo
• Modificare prodotti esistenti (prezzo, descrizione, IVA, ecc.)
• Visualizzare l’elenco completo dei prodotti
• Cancellare prodotti dal catalogo (con richiesta di conferma)
• Visualizzare gli ordini complessivi
• Filtrare gli ordini per intervallo di date (dalla data – alla data)
• Filtrare gli ordini per cliente



Sicurezza
La sicurezza dell’applicazione è un aspetto fondamentale del progetto. Devono essere implementate le
seguenti misure:
• Prevenzione SQL Injection: Utilizzare PreparedStatement per tutte le query al database. Mai
concatenare stringhe nelle query SQL.
• Cifratura delle password: Le password degli utenti devono essere salvate nel database in forma
cifrata.
• Filtri: Utilizzare i filtri servlet per il controllo degli accessi e altre operazioni trasversali.
• Gestione errori: Il server non deve mai perdere il controllo in caso di errore. Configurare pagine di
errore personalizzate tramite web.xml per i diversi codici di errore (404, 500, 403, ecc.)



Design Responsivo
Il sito deve essere completamente responsive, adattandosi correttamente a dispositivi desktop, tablet e
smartphone.



Validazione Form
Tutti i form devono essere controllati lato client con JavaScript prima dell’invio al server:
• Utilizzare espressioni regolari per validare i campi di input
• Impostare il focus sul campo in cui l’utente sta scrivendo
• Visualizzare istruzioni di compilazione nel placeholder di ogni campo
• Mostrare messaggi di errore inline quando l’utente preme submit (NO alert)
• Il form viene inviato al server solo se tutti i campi sono corretti



AJAX e Comunicazione Asincrona
Utilizzare AJAX per scambiare piccole informazioni con il server in formato JSON. Implementazioni minime
richieste:
• Barra di ricerca con suggerimenti dinamici
• Controllo in tempo reale durante la registrazione che l’email non sia già presente nel database



Fragment JSP
Utilizzare i fragment con include nelle pagine JSP per creare componenti riutilizzabili: header, footer e
menu di navigazione.



Messaggi di Conferma
Fornire sempre conferma all’utente per le azioni eseguite con successo: registrazione completata, prodotto
aggiunto al carrello, ordine effettuato, prodotto inserito/modificato nel catalogo



Conferme di Cancellazione
Quando l’amministratore esegue operazioni di cancellazione, chiedere sempre conferma prima di
procedere.



Pagine di Errore
Gestire le pagine di errore tramite configurazione in web.xml. Il server non deve mai perdere il controllo.
Prevedere pagine personalizzate per: risorsa non trovata (404), errore del server (500), mancanza di
permessi (403).



Checklist Completa
✔ Requisito Priorità
☐ Catalogo prodotti con visualizzazione dettagliata OBBLIGATORIO
☐ Carrello: aggiunta, modifica quantità, rimozione OBBLIGATORIO
☐ Conferma ordine e svuotamento carrello OBBLIGATORIO
☐ Storici ordini effettuati dal cliente OBBLIGATORIO
☐ Barra di ricerca con AJAX CONSIGLIATO
☐ CRUD completo prodotti (inserisci/modifica/visualizza/cancella) OBBLIGATORIO
☐ Visualizzazione ordini complessivi OBBLIGATORIO
☐ Filtro ordini per intervallo di date ALMENO 1
☐ Filtro ordini per cliente ALMENO 1
☐ Conferma prima di cancellare un prodotto OBBLIGATORIO
☐ Prezzo e IVA salvati nella riga d’ordine (integrità storica) OBBLIGATORIO
☐ Vincolo d’integrità referenziale (prodotti cancellati negli ordini) OBBLIGATORIO
☐ DataSource o DriverManager + Connection Pool OBBLIGATORIO
☐ Prevenzione SQL Injection CONSIGLIATO
☐ Cifratura delle password OBBLIGATORIO
☐ Autenticazione programmata per area admin OBBLIGATORIO
☐ Utilizzo dei filtri servlet OBBLIGATORIO
☐ Pattern MVC rispettato OBBLIGATORIO
☐ Package Control (Servlet) e Model (Bean, Carrello) OBBLIGATORIO
☐ HTML generato solo da JSP (mai dalle Servlet) OBBLIGATORIO
☐ Fragment JSP per header, footer e menu OBBLIGATORIO
☐ Gestione sessioni per il carrello OBBLIGATORIO
☐ Sito responsive OBBLIGATORIO
☐ Validazione form con regex e JavaScript CONSIGLIATO
☐ Focus sul campo attivo e placeholder descrittivi OPZIONALE
☐ Messaggi di errore inline (no alert) OBBLIGATORIO
☐ AJAX: barra di ricerca con suggerimenti ALMENO 1
Tecnologie Software per il Web – Progetto d’Esame a.a. 2025-2026 Resto 1
Pagina 4
☐ AJAX: verifica email già presente in fase di registrazione OBBLIGATORIO
☐ Fetch API con JSON per comunicazioni asincrone OBBLIGATORIO
☐ Pagine di errore personalizzate (web.xml) OBBLIGATORIO
☐ Messaggi di conferma per le azioni dell’utente OBBLIGATORIO
☐ Tomcat come server diretto (no deploy esterno) OBBLIGATORIO
☐ Fattura tramite media query OBBLIGATORIO



Note Finali
Questo documento riassume tutti i requisiti per il superamento della discussione del progetto d’esame. Si
raccomanda di seguire ogni punto della checklist e di verificare che ogni componente del gruppo sia in
grado di illustrare tutte le funzionalità implementate.
