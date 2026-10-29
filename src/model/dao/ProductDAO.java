package model.dao;

import model.Product;
import util.SqlUtil;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** DAO: unico posto che contiene le query SQL sui prodotti. */
public class ProductDAO {

    private static final String COLUMNS =
            "id, nome, descrizione, prezzo, iva, giacenza, attivo, immagine";

    /** Prodotti visibili nel catalogo (attivo = true), in ordine alfabetico. */
    public List<Product> findAllActive() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti WHERE attivo = ? ORDER BY nome";
        List<Product> result = new ArrayList<>();

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }
        return result;
    }

    /**
     * Prodotti attivi il cui nome contiene il testo cercato (senza distinguere
     * maiuscole/minuscole). Il testo viene passato come parametro, mai concatenato.
     */
    public List<Product> findActiveByName(String text) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti "
                   + "WHERE attivo = ? AND nome ILIKE ? ESCAPE '\\' ORDER BY nome";
        List<Product> result = new ArrayList<>();

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setString(2, "%" + SqlUtil.escapeLike(text) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }
        return result;
    }

    /**
     * Suggerimenti per la ricerca: al massimo "limit" prodotti attivi il cui nome contiene il testo.
     * Prima quelli che INIZIANO col testo, poi gli altri, ciascun gruppo in ordine alfabetico.
     * LIMIT lo fa il database: non si caricano tutti i prodotti per poi buttarne via la maggior parte.
     */
    public List<Product> suggestByName(String text, int limit) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti "
                   + "WHERE attivo = ? AND nome ILIKE ? ESCAPE '\\' "
                   + "ORDER BY (nome ILIKE ? ESCAPE '\\') DESC, nome LIMIT ?";
        List<Product> result = new ArrayList<>();
        String escaped = SqlUtil.escapeLike(text);

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setString(2, "%" + escaped + "%");
            ps.setString(3, escaped + "%");
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }
        return result;
    }

    /** Un prodotto attivo dato il suo id; null se non esiste o non è più a catalogo. */
    public Product findActiveById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti WHERE id = ? AND attivo = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setBoolean(2, true);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * I prodotti attivi tra gli id indicati, in una sola query (serve al carrello).
     * "id = ANY (?)" con un array PostgreSQL equivale a "id IN (1, 2, 3)" ma con un solo parametro.
     */
    public List<Product> findActiveByIds(Collection<Integer> ids) throws SQLException {
        List<Product> result = new ArrayList<>();
        if (ids.isEmpty()) {
            return result;
        }
        String sql = "SELECT " + COLUMNS + " FROM prodotti WHERE id = ANY (?) AND attivo = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            Array array = con.createArrayOf("integer", ids.toArray(new Integer[0]));
            ps.setArray(1, array);
            ps.setBoolean(2, true);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            array.free();
        }
        return result;
    }

    /**
     * Scala la giacenza DENTRO una transazione (usa la Connection ricevuta, non ne apre una nuova).
     * UPDATE condizionato: la giacenza scende solo se ce n'è abbastanza (e il prodotto è attivo).
     * Il controllo e la modifica avvengono in UN'unica istruzione atomica: due clienti insieme non possono
     * comprare lo stesso ultimo pezzo. RETURNING restituisce i dati del prodotto proprio come erano
     * in quel momento (nome, prezzo, IVA). Se la giacenza non basta, nessuna riga cambia e torna null.
     */
    public Product scalaGiacenza(Connection con, int id, int quantita) throws SQLException {
        String sql = "UPDATE prodotti SET giacenza = giacenza - ? "
                   + "WHERE id = ? AND attivo = ? AND giacenza >= ? "
                   + "RETURNING " + COLUMNS;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantita);
            ps.setInt(2, id);
            ps.setBoolean(3, true);
            ps.setInt(4, quantita);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    // ------------------------------------------------------------------ area amministratore

    /** TUTTI i prodotti, anche quelli eliminati (attivo = false): serve all'elenco dell'admin. */
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti ORDER BY attivo DESC, nome";
        List<Product> result = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    /** Un prodotto dato l'id, attivo o no; null se non esiste. */
    public Product findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM prodotti WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Inserisce un nuovo prodotto (sempre attivo) e restituisce l'id generato. */
    public int insert(Product p) throws SQLException {
        String sql = "INSERT INTO prodotti (nome, descrizione, prezzo, iva, giacenza, immagine) "
                   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setBigDecimal(3, p.getPrezzo());
            ps.setBigDecimal(4, p.getIva());
            ps.setInt(5, p.getGiacenza());
            ps.setString(6, p.getImmagine());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Aggiorna i dati di un prodotto (non tocca "attivo"). Restituisce le righe modificate (0 = id inesistente). */
    public int update(Product p) throws SQLException {
        String sql = "UPDATE prodotti SET nome = ?, descrizione = ?, prezzo = ?, iva = ?, giacenza = ?, immagine = ? "
                   + "WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setBigDecimal(3, p.getPrezzo());
            ps.setBigDecimal(4, p.getIva());
            ps.setInt(5, p.getGiacenza());
            ps.setString(6, p.getImmagine());
            ps.setInt(7, p.getId());
            return ps.executeUpdate();
        }
    }

    /**
     * Eliminazione LOGICA (soft delete): il prodotto resta nel database con attivo = false.
     * Serve perché gli ordini già fatti lo referenziano (e le fatture devono continuare a mostrarlo).
     * Con attivo = true lo si ripristina. Restituisce le righe modificate.
     */
    public int setAttivo(int id, boolean attivo) throws SQLException {
        String sql = "UPDATE prodotti SET attivo = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, attivo);
            ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescrizione(rs.getString("descrizione"));
        p.setPrezzo(rs.getBigDecimal("prezzo"));
        p.setIva(rs.getBigDecimal("iva"));
        p.setGiacenza(rs.getInt("giacenza"));
        p.setAttivo(rs.getBoolean("attivo"));
        p.setImmagine(rs.getString("immagine"));
        return p;
    }
}
