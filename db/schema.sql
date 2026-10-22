-- Schema database e-commerce (PostgreSQL)
-- Riesecuzione sicura: le tabelle vengono eliminate in ordine inverso alle dipendenze.

DROP TABLE IF EXISTS ordini_prodotti;
DROP TABLE IF EXISTS ordini;
DROP TABLE IF EXISTS prodotti;
DROP TABLE IF EXISTS utenti;

CREATE TABLE utenti (
    id       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    cognome  VARCHAR(100) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,            -- contiene l'hash, mai la password in chiaro
    ruolo    VARCHAR(10)  NOT NULL DEFAULT 'cliente'
             CHECK (ruolo IN ('cliente', 'admin'))
);

CREATE TABLE prodotti (
    id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome        VARCHAR(150)  NOT NULL,
    descrizione TEXT          NOT NULL,
    prezzo      NUMERIC(10,2) NOT NULL CHECK (prezzo >= 0),
    iva         NUMERIC(4,2)  NOT NULL CHECK (iva >= 0),
    giacenza    INTEGER       NOT NULL DEFAULT 0 CHECK (giacenza >= 0),  -- pezzi disponibili (0 = non acquistabile)
    attivo      BOOLEAN       NOT NULL DEFAULT TRUE,   -- soft delete
    immagine    VARCHAR(255)                            -- facoltativa: nome del file
);

CREATE TABLE ordini (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_utente            INTEGER      NOT NULL REFERENCES utenti(id) ON DELETE RESTRICT,
    data_ordine          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    indirizzo_spedizione VARCHAR(255) NOT NULL
);

CREATE TABLE ordini_prodotti (
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ordine       INTEGER       NOT NULL REFERENCES ordini(id) ON DELETE CASCADE,
    id_prodotto     INTEGER       NOT NULL REFERENCES prodotti(id) ON DELETE RESTRICT,
    quantita        INTEGER       NOT NULL CHECK (quantita > 0),
    prezzo_unitario NUMERIC(10,2) NOT NULL CHECK (prezzo_unitario >= 0),  -- prezzo al momento dell'acquisto
    iva             NUMERIC(4,2)  NOT NULL CHECK (iva >= 0),              -- IVA al momento dell'acquisto
    UNIQUE (id_ordine, id_prodotto)
);
