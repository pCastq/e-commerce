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

    /** Prodotti visibili nel catalogo (attivo = true). */
    public List<Product> findAllActive() throws SQLException {
        String sql = "SELECT id, nome, descrizione, prezzo, iva, giacenza, attivo, immagine "
                   + "FROM prodotti WHERE attivo = ? ORDER BY nome";
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
