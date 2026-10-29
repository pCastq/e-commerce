package model.service;

import model.Cart;
import model.Product;
import model.User;
import model.dao.Database;
import model.dao.OrdineDAO;
import model.dao.ProductDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * SERVICE degli ordini: contiene la TRANSAZIONE della conferma ordine.
 *
 * Una transazione è un gruppo di operazioni sul database "tutte o nessuna":
 * ordine + righe + scalo della giacenza devono riuscire insieme, altrimenti non deve restare nulla
 * (niente ordine senza righe, niente giacenza scalata per un ordine mai creato).
 * Per questo si usa UNA sola Connection con auto-commit spento, e alla fine commit() oppure rollback().
 */
public class OrdineService {

    private static final int INDIRIZZO_MIN = 10;
    private static final int INDIRIZZO_MAX = 255;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrdineDAO ordineDAO = new OrdineDAO();

    /** Perché un ordine non è stato creato. */
    public enum Problema { INDIRIZZO_NON_VALIDO, CARRELLO_VUOTO, NON_DISPONIBILE }

    /** Esito: se ok() c'è l'id dell'ordine; altrimenti il problema e il messaggio da mostrare. */
    public record Risultato(Integer idOrdine, Problema problema, String messaggio) {
        public boolean ok() { return idOrdine != null; }
    }

    public static boolean indirizzoValido(String indirizzo) {
        return indirizzo != null
                && indirizzo.length() >= INDIRIZZO_MIN
                && indirizzo.length() <= INDIRIZZO_MAX
                && indirizzo.chars().noneMatch(Character::isISOControl);
    }

    /**
     * Trasforma il carrello in un ordine. Se riesce, svuota il carrello.
     * synchronized (cart): se lo stesso utente invia due volte il form (doppio clic), la seconda richiesta
     * aspetta la prima e poi trova il carrello vuoto: non nascono due ordini.
     */
    public Risultato conferma(User utente, Cart cart, String indirizzoGrezzo) throws SQLException {
        String indirizzo = indirizzoGrezzo == null ? "" : indirizzoGrezzo.trim();
        if (!indirizzoValido(indirizzo)) {
            return new Risultato(null, Problema.INDIRIZZO_NON_VALIDO,
                    "Inserisci l'indirizzo di spedizione completo (da " + INDIRIZZO_MIN + " a " + INDIRIZZO_MAX + " caratteri).");
        }

        synchronized (cart) {
            // TreeMap: prodotti sempre in ordine di id. Due ordini contemporanei bloccano le righe nello
            // stesso ordine e così non possono bloccarsi a vicenda (deadlock).
            Map<Integer, Integer> righe = new TreeMap<>(cart.getItems());
            if (righe.isEmpty()) {
                return new Risultato(null, Problema.CARRELLO_VUOTO, "Il carrello è vuoto.");
            }

            int idOrdine;
            Integer prodottoMancante = null;

            try (Connection con = Database.getConnection()) {
                con.setAutoCommit(false);                       // inizio transazione
                try {
                    idOrdine = ordineDAO.insert(con, utente.getId(), indirizzo);

                    for (Map.Entry<Integer, Integer> e : righe.entrySet()) {
                        Product p = productDAO.scalaGiacenza(con, e.getKey(), e.getValue());
                        if (p == null) {                        // giacenza insufficiente o prodotto non più attivo
                            prodottoMancante = e.getKey();
                            break;
                        }
                        // prezzo e IVA salvati nella riga: sono quelli di questo momento
                        ordineDAO.insertRiga(con, idOrdine, p.getId(), e.getValue(), p.getPrezzo(), p.getIva());
                    }

                    if (prodottoMancante != null) {
                        con.rollback();                         // annulla TUTTO: ordine e giacenze già scalate
                    } else {
                        con.commit();                           // rende definitivo TUTTO insieme
                    }
                } catch (SQLException | RuntimeException ex) {
                    con.rollback();
                    throw ex;
                } finally {
                    con.setAutoCommit(true);                    // la connessione torna al pool come l'abbiamo trovata
                }
            }

            if (prodottoMancante != null) {
                Product p = productDAO.findActiveById(prodottoMancante);
                String msg = (p == null)
                        ? "Un prodotto del carrello non è più disponibile: l'ordine non è stato effettuato."
                        : "«" + p.getNome() + "» non è più disponibile nella quantità richiesta (disponibili: "
                          + p.getGiacenza() + "): l'ordine non è stato effettuato.";
                return new Risultato(null, Problema.NON_DISPONIBILE, msg);
            }

            cart.clear();
            return new Risultato(idOrdine, null, null);
        }
    }
}
