package model.service;

import model.Cart;
import model.Ordine;
import model.Product;
import model.User;
import model.dao.Database;
import model.dao.OrdineDAO;
import model.dao.ProductDAO;

import java.sql.Connection;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
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

    /** Gli ordini dell'utente, dal più recente. */
    public List<Ordine> ordiniDi(User utente) throws SQLException {
        return ordineDAO.findByUser(utente.getId());
    }

    /** Un ordine dell'utente (null se non esiste o è di un altro: non si distingue di proposito). */
    public Ordine trovaDi(int idOrdine, User utente) throws SQLException {
        return ordineDAO.findByIdAndUser(idOrdine, utente.getId());
    }

    // ------------------------------------------------------------------ area amministratore

    /** Massimo di ordini mostrati in una ricerca (oltre, si invita a restringere i filtri). */
    public static final int LIMITE_RICERCA = 200;

    /**
     * Risultato di una ricerca ordini: gli ordini trovati, gli errori sui filtri (per campo; se non vuota non si è cercato),
     * se l'elenco è stato troncato al limite e il totale incassato degli ordini mostrati (IVA inclusa).
     */
    public record Ricerca(List<Ordine> ordini, Map<String, String> errori, boolean troncato, BigDecimal totale) {
        public boolean ok() { return errori.isEmpty(); }
    }

    /**
     * Cerca gli ordini di tutti i clienti. I parametri sono i testi dei filtri così come arrivano dal form
     * (null o vuoti = filtro non usato): date nel formato aaaa-mm-gg (quello di &lt;input type="date"&gt;).
     */
    public Ricerca cerca(String dal, String al, String cliente) throws SQLException {
        Map<String, String> errori = new LinkedHashMap<>();
        LocalDate dataDal = leggiData(dal, "dal", errori);
        LocalDate dataAl = leggiData(al, "al", errori);
        if (dataDal != null && dataAl != null && dataDal.isAfter(dataAl)) {
            errori.put("al", "La data finale non può essere precedente a quella iniziale.");
        }
        String testo = cliente == null ? "" : cliente.trim();
        if (testo.length() > 100) {
            errori.put("cliente", "Testo troppo lungo (massimo 100 caratteri).");
        }
        if (!errori.isEmpty()) {
            return new Ricerca(List.of(), errori, false, BigDecimal.ZERO);
        }

        // si chiede un ordine in più del limite per sapere se ce ne sono altri (senza contarli tutti)
        List<Ordine> trovati = ordineDAO.search(dataDal, dataAl, testo, LIMITE_RICERCA + 1);
        boolean troncato = trovati.size() > LIMITE_RICERCA;
        if (troncato) {
            trovati = trovati.subList(0, LIMITE_RICERCA);
        }
        BigDecimal totale = BigDecimal.ZERO;
        for (Ordine o : trovati) {
            totale = totale.add(o.getTotale());
        }
        return new Ricerca(trovati, errori, troncato, totale);
    }

    /** Un ordine qualunque, con i dati del cliente (solo per l'admin); null se non esiste. */
    public Ordine trovaPerAdmin(int idOrdine) throws SQLException {
        return ordineDAO.findByIdAdmin(idOrdine);
    }

    private static LocalDate leggiData(String testo, String campo, Map<String, String> errori) {
        if (testo == null || testo.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(testo.trim());          // formato ISO: 2026-10-29
        } catch (DateTimeParseException e) {
            errori.put(campo, "Data non valida.");
            return null;
        }
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
                return new Risultato(null, Problema.CARRELLO_VUOTO, CartService.MSG_CARRELLO_VUOTO);
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
