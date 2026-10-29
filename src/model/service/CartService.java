package model.service;

import model.Cart;
import model.CartLine;
import model.Product;
import model.dao.ProductDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE del carrello: le regole su quantità, giacenza e disponibilità dei prodotti.
 * Lavora sul {@link Cart} che gli viene passato (quello della sessione) e non conosce HTTP.
 */
public class CartService {

    private final ProductDAO productDAO = new ProductDAO();

    /** OK = fatto; AVVISO = fatto ma con una correzione; ERRORE = non eseguito. */
    public enum Stato { OK, AVVISO, ERRORE }

    /** Esito di un'operazione: stato e messaggio da mostrare all'utente. */
    public record Esito(Stato stato, String messaggio) { }

    /** Righe da mostrare, totale (IVA inclusa) e un eventuale avviso. */
    public record Riepilogo(List<CartLine> righe, BigDecimal totale, String avviso) {
        public boolean vuoto() { return righe.isEmpty(); }
    }

    /**
     * Risultato di un aggiornamento di quantità, con tutto ciò che serve a rispondere in AJAX.
     * Se esito.stato() è ERRORE gli altri campi sono vuoti (null / 0).
     * riga: la riga aggiornata (null se il prodotto è stato rimosso); messaggio: cosa dire all'utente (o null).
     */
    public record Aggiornamento(Esito esito, Riepilogo riepilogo, CartLine riga, String messaggio, int totaleArticoli) { }

    public static final String MSG_CARRELLO_VUOTO = "Il carrello è vuoto.";

    /**
     * Aggiunge al carrello (sommando a quanto già presente).
     * id e quantita sono Integer: null significa "valore non numerico" (lo decide chi ha letto la richiesta).
     */
    public Esito aggiungi(Cart cart, Integer id, Integer quantita) throws SQLException {
        if (id == null || !quantitaValida(quantita)) {
            return new Esito(Stato.ERRORE, "Dati non validi.");
        }
        Product p = productDAO.findActiveById(id);
        if (p == null) {
            return new Esito(Stato.ERRORE, "Prodotto non disponibile.");
        }
        if (p.getGiacenza() <= 0) {
            return new Esito(Stato.ERRORE, "«" + p.getNome() + "» è esaurito.");
        }

        int nuova = Math.min(cart.getQuantita(id) + quantita, Cart.MAX_QUANTITA);
        if (nuova > p.getGiacenza()) {
            cart.set(id, p.getGiacenza());
            return quantitaAdeguata(p.getGiacenza());
        }
        cart.set(id, nuova);
        return new Esito(Stato.OK, "«" + p.getNome() + "» aggiunto al carrello.");
    }

    /** Imposta la quantità di un prodotto già nel carrello (valore assoluto, mai "+1"). */
    public Esito aggiorna(Cart cart, Integer id, Integer quantita) throws SQLException {
        if (id == null || !quantitaValida(quantita) || cart.getQuantita(id) == 0) {
            return new Esito(Stato.ERRORE, "Dati non validi.");
        }
        Product p = productDAO.findActiveById(id);
        if (p == null || p.getGiacenza() <= 0) {
            cart.remove(id);
            return new Esito(Stato.AVVISO, "Prodotto non più disponibile: rimosso dal carrello.");
        }
        if (quantita > p.getGiacenza()) {
            cart.set(id, p.getGiacenza());
            return quantitaAdeguata(p.getGiacenza());
        }
        cart.set(id, quantita);
        return new Esito(Stato.OK, "Quantità aggiornata.");
    }

    /**
     * Aggiorna la quantità e subito dopo rilegge il carrello dal database: restituisce in un colpo solo
     * l'esito, la riga aggiornata, i totali e il messaggio. È ciò che serve alla risposta AJAX.
     */
    public Aggiornamento aggiornaERiepiloga(Cart cart, Integer id, Integer quantita) throws SQLException {
        Esito esito = aggiorna(cart, id, quantita);
        if (esito.stato() == Stato.ERRORE) {
            return new Aggiornamento(esito, null, null, null, cart.getTotalItems());
        }
        Riepilogo r = riepilogo(cart);          // riallinea anche il carrello (es. altri prodotti esauriti nel frattempo)
        CartLine riga = null;
        for (CartLine l : r.righe()) {
            if (l.getProdotto().getId() == id) {
                riga = l;
            }
        }
        String messaggio = (esito.stato() == Stato.AVVISO) ? esito.messaggio() : r.avviso();
        return new Aggiornamento(esito, r, riga, messaggio, cart.getTotalItems());
    }

    /** Toglie un prodotto dal carrello (id null = non numerico: non fa nulla). */
    public void rimuovi(Cart cart, Integer id) {
        if (id != null) {
            cart.remove(id);
        }
    }

    /** Svuota il carrello. */
    public void svuota(Cart cart) {
        cart.clear();
    }

    /**
     * Costruisce le righe del carrello leggendo i prodotti dal database in una sola query, e riallinea
     * il carrello: toglie i prodotti non più disponibili e riduce le quantità oltre la giacenza.
     * cart può essere null (visitatore che non ha ancora un carrello).
     */
    public Riepilogo riepilogo(Cart cart) throws SQLException {
        List<CartLine> righe = new ArrayList<>();
        BigDecimal totale = BigDecimal.ZERO;
        String avviso = null;

        if (cart != null && !cart.isEmpty()) {
            Map<Integer, Product> prodotti = new HashMap<>();
            for (Product p : productDAO.findActiveByIds(cart.getItems().keySet())) {
                prodotti.put(p.getId(), p);
            }

            for (Map.Entry<Integer, Integer> e : cart.getItems().entrySet()) {
                Product p = prodotti.get(e.getKey());
                int quantita = e.getValue();

                if (p == null || p.getGiacenza() <= 0) {
                    cart.remove(e.getKey());
                    avviso = "Alcuni prodotti non sono più disponibili e sono stati rimossi dal carrello.";
                    continue;
                }
                if (quantita > p.getGiacenza()) {
                    quantita = p.getGiacenza();
                    cart.set(p.getId(), quantita);
                    avviso = "Alcune quantità sono state ridotte in base alla disponibilità.";
                }
                CartLine riga = new CartLine(p, quantita);
                righe.add(riga);
                totale = totale.add(riga.getSubtotale());
            }
        }
        return new Riepilogo(righe, totale, avviso);
    }

    /** Avviso uguale in catalogo e carrello quando si chiede più di quanto c'è in magazzino. */
    private static Esito quantitaAdeguata(int giacenza) {
        return new Esito(Stato.AVVISO, soloDisponibili(giacenza) + ".");
    }

    /** "Disponibile solo 1 pezzo" / "Disponibili solo 5 pezzi": singolare e plurale corretti. */
    private static String soloDisponibili(int n) {
        return n == 1 ? "Disponibile solo 1 pezzo" : "Disponibili solo " + n + " pezzi";
    }

    /** Quantità valida: intero da 1 a Cart.MAX_QUANTITA. */
    private static boolean quantitaValida(Integer q) {
        return q != null && q >= 1 && q <= Cart.MAX_QUANTITA;
    }
}
