package model.dao;

import model.Product;

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
            ps.setString(2, "%" + escapeLike(text) + "%");
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

    /** Neutralizza % _ e \ che in LIKE hanno un significato speciale. */
    private static String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
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
