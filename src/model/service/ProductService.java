package model.service;

import model.Product;
import model.dao.ProductDAO;

import java.sql.SQLException;
import java.util.List;

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

    /** Un prodotto a catalogo, oppure null se non esiste o non è più attivo. */
    public Product trovaAttivo(int id) throws SQLException {
        return productDAO.findActiveById(id);
    }
}
