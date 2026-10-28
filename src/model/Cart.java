package model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Il carrello di un visitatore. Vive nella SESSIONE (attributo "carrello"), non nel database.
 * Contiene solo coppie idProdotto -> quantità: nome, prezzo e immagine si rileggono dal database
 * quando serve, così si mostrano sempre i dati aggiornati.
 *
 * Serializable: Tomcat può salvare le sessioni su disco (es. al riavvio), quindi gli oggetti
 * messi in sessione devono poter essere trasformati in byte.
 * I metodi sono synchronized perché la stessa sessione può ricevere due richieste insieme
 * (es. doppio clic sul pulsante).
 */
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Quantità massima per singolo prodotto. */
    public static final int MAX_QUANTITA = 99;

    private final Map<Integer, Integer> items = new LinkedHashMap<>();   // mantiene l'ordine di inserimento

    /** Imposta la quantità di un prodotto (se è già nel carrello la sostituisce). */
    public synchronized void set(int idProdotto, int quantita) {
        if (quantita <= 0) {
            items.remove(idProdotto);
        } else {
            items.put(idProdotto, quantita);
        }
    }

    public synchronized void remove(int idProdotto) {
        items.remove(idProdotto);
    }

    public synchronized void clear() {
        items.clear();
    }

    /** Quantità di un prodotto nel carrello (0 se non c'è). */
    public synchronized int getQuantita(int idProdotto) {
        return items.getOrDefault(idProdotto, 0);
    }

    /** Copia del contenuto (id -> quantità), sicura da scorrere anche se il carrello cambia. */
    public synchronized Map<Integer, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }

    /** Numero totale di pezzi (somma delle quantità): lo mostra il menu. Usabile in EL come ${carrello.totalItems}. */
    public synchronized int getTotalItems() {
        int tot = 0;
        for (int q : items.values()) {
            tot += q;
        }
        return tot;
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }
}
