package model.service;

import model.Product;
import model.dao.ProductDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SERVICE dei prodotti: la logica di business sul catalogo.
 * Non conosce HTTP (niente request/response): le servlet lo chiamano e decidono cosa mostrare.
 * Non ha campi modificabili: una sola istanza può essere usata da più thread insieme.
 */
public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    /** Prodotti a catalogo; se c'è un testo di ricerca, solo quelli il cui nome lo contiene. */
    public List<Product> catalogo(String ricerca) throws SQLException {
        String q = (ricerca == null) ? "" : ricerca.trim();
        return q.isEmpty() ? productDAO.findAllActive() : productDAO.findActiveByName(q);
    }

    /** Lunghezza minima del testo per cercare suggerimenti (con 1 lettera i risultati sarebbero troppi e inutili). */
    public static final int SUGGERIMENTI_MIN_CARATTERI = 2;
    private static final int SUGGERIMENTI_MAX = 6;

    /** Pochi prodotti che corrispondono al testo parziale: serve al menu a tendina della ricerca. */
    public List<Product> suggerimenti(String testo) throws SQLException {
        String q = (testo == null) ? "" : testo.trim();
        if (q.length() < SUGGERIMENTI_MIN_CARATTERI || q.length() > 100) {
            return List.of();
        }
        return productDAO.suggestByName(q, SUGGERIMENTI_MAX);
    }

    // ------------------------------------------------------------------ area amministratore

    /** Aliquote IVA ammesse in Italia (%). Il form offre solo queste. */
    public static final List<Integer> ALIQUOTE_IVA = List.of(4, 5, 10, 22);

    private static final Pattern PREZZO = Pattern.compile("^\\d{1,6}([.,]\\d{1,2})?$");
    private static final Pattern INTERO = Pattern.compile("^\\d{1,6}$");
    private static final Pattern FILE_IMMAGINE =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]{0,99}\\.(jpg|jpeg|png|webp|gif|svg)$", Pattern.CASE_INSENSITIVE);

    /** Esito di un salvataggio: errori per campo (vuota se ok), i valori ripuliti e l'id del prodotto salvato. */
    public record SalvaRisultato(Map<String, String> errori, Product prodotto) {
        public boolean ok() { return errori.isEmpty(); }
    }

    /** I campi del form prodotto, nell'ordine in cui li gestisce salva(). */
    public static final List<String> CAMPI_FORM =
            List.of("nome", "descrizione", "prezzo", "iva", "giacenza", "immagine");

    /**
     * I valori iniziali del form, come testi: quelli di un prodotto esistente (modifica) oppure,
     * con p == null, i valori predefiniti di un prodotto nuovo (IVA 22%, giacenza 0).
     * Il prezzo si mostra con la virgola, come lo scrive un utente italiano (e come salva() lo accetta).
     */
    public Map<String, String> valoriForm(Product p) {
        Map<String, String> v = new LinkedHashMap<>();
        if (p == null) {
            v.put("iva", "22");
            v.put("giacenza", "0");
            return v;
        }
        v.put("id", String.valueOf(p.getId()));
        v.put("nome", p.getNome());
        v.put("descrizione", p.getDescrizione());
        v.put("prezzo", p.getPrezzo().toPlainString().replace('.', ','));
        v.put("iva", String.valueOf(p.getIva().intValue()));
        v.put("giacenza", String.valueOf(p.getGiacenza()));
        v.put("immagine", p.getImmagine() == null ? "" : p.getImmagine());
        return v;
    }

    /** Tutti i prodotti, anche quelli eliminati (per l'elenco dell'admin). */
    public List<Product> tutti() throws SQLException {
        return productDAO.findAll();
    }

    /** Un prodotto qualunque sia il suo stato (per modificarlo); null se non esiste. */
    public Product trova(int id) throws SQLException {
        return productDAO.findById(id);
    }

    /**
     * Crea (id == null) o modifica (id != null) un prodotto. Riceve i testi del form così come sono, li valida
     * e li converte; se qualcosa non va restituisce gli errori e NON salva.
     */
    public SalvaRisultato salva(Integer id, String nome, String descrizione, String prezzo,
                                String iva, String giacenza, String immagine) throws SQLException {
        Map<String, String> errori = new LinkedHashMap<>();
        Product p = new Product();
        if (id != null) {
            p.setId(id);
        }

        String n = nome == null ? "" : nome.trim();
        if (n.length() < 2 || n.length() > 150) {
            errori.put("nome", "Il nome deve avere da 2 a 150 caratteri.");
        }
        p.setNome(n);

        String d = descrizione == null ? "" : descrizione.trim();
        if (d.isEmpty() || d.length() > 2000) {
            errori.put("descrizione", "La descrizione è obbligatoria (massimo 2000 caratteri).");
        }
        p.setDescrizione(d);

        String pr = prezzo == null ? "" : prezzo.trim();
        if (!PREZZO.matcher(pr).matches() || new BigDecimal(pr.replace(',', '.')).signum() <= 0) {
            errori.put("prezzo", "Inserisci un prezzo maggiore di zero, con al massimo 2 decimali (es. 2,49).");
        } else {
            p.setPrezzo(new BigDecimal(pr.replace(',', '.')));
        }

        try {
            int aliquota = Integer.parseInt(iva == null ? "" : iva.trim());
            if (!ALIQUOTE_IVA.contains(aliquota)) {
                throw new NumberFormatException();
            }
            p.setIva(BigDecimal.valueOf(aliquota));
        } catch (NumberFormatException e) {
            errori.put("iva", "Scegli un'aliquota IVA tra quelle proposte.");
        }

        String g = giacenza == null ? "" : giacenza.trim();
        if (!INTERO.matcher(g).matches()) {
            errori.put("giacenza", "La giacenza deve essere un numero intero da 0 in su.");
        } else {
            p.setGiacenza(Integer.parseInt(g));
        }

        String img = immagine == null ? "" : immagine.trim();
        if (img.isEmpty()) {
            p.setImmagine(null);
        } else if (!FILE_IMMAGINE.matcher(img).matches()) {
            errori.put("immagine", "Nome di file non valido (es. pasta.jpg: solo lettere, numeri, punto, trattino; estensione jpg, png, webp, gif o svg).");
        } else {
            p.setImmagine(img);
        }

        if (!errori.isEmpty()) {
            return new SalvaRisultato(errori, p);
        }

        if (id == null) {
            p.setId(productDAO.insert(p));
        } else if (productDAO.update(p) == 0) {
            errori.put("generale", "Prodotto non trovato.");
        }
        return new SalvaRisultato(errori, p);
    }

    /** Eliminazione logica: il prodotto sparisce dal catalogo ma resta nel database (storico ordini). */
    public boolean elimina(int id) throws SQLException {
        return productDAO.setAttivo(id, false) > 0;
    }

    /** Annulla l'eliminazione logica. */
    public boolean ripristina(int id) throws SQLException {
        return productDAO.setAttivo(id, true) > 0;
    }

    /** Un prodotto a catalogo, oppure null se non esiste o non è più attivo. */
    public Product trovaAttivo(int id) throws SQLException {
        return productDAO.findActiveById(id);
    }
}
