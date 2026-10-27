package model.dao;

import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
